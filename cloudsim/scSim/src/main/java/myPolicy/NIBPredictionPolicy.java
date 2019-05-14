package myPolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

public class NIBPredictionPolicy extends VmAllocationPolicy {

    //To track the Host for each Vm. The string is the unique Vm identifier, composed by its id and its userId
    private Map<String, Host> vmTable;

    public NIBPredictionPolicy(List<? extends Host> list) {
        super(list);
        vmTable = new HashMap<>();
    }

    public Host getHost(Vm vm) {
        // We must recover the Host which hosting Vm
        return this.vmTable.get(vm.getUid());
    }

    public Host getHost(int vmId, int userId) {
        // We must recover the Host which hosting Vm
        return this.vmTable.get(Vm.getUid(userId, vmId));
    }

    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) {
            //the host is appropriate, we track it
            vmTable.put(vm.getUid(), host);
            return true;
        }
        return false;
    }

    public boolean allocateHostForVm(Vm vm) {

        Collections.sort(getHostList(), new Comparator<Host>() {
            @Override
            public int compare(Host h1, Host h2) {
                return (int) (h1.getAvailableMips() - h2.getAvailableMips());
            }
        });


        for (Host h : getHostList()) {
            if (h.vmCreate(vm)) {
                //track the host
                vmTable.put(vm.getUid(), h);
                return true;
            }
        }
        return false;
    }

    public void deallocateHostForVm(Vm vm, Host host) {
        vmTable.remove(vm.getUid());
        host.vmDestroy(vm);
    }

    @Override
    public void deallocateHostForVm(Vm v) {
        //get the host and remove the vm
        vmTable.get(v.getUid()).vmDestroy(v);
    }

    public static Object optimizeAllocation() {
        return null;
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vms) {

        List<Map<String, Object>> map = new ArrayList<Map<String, Object>>();

        Set<Host> morePowerfulHosts = new HashSet<Host>();
        List<Vm> unAllocatedVMs = new ArrayList<Vm>();
        unAllocatedVMs.addAll(vms);

        for (Vm v : vms)
            morePowerfulHosts.add(v.getHost());

        Set<Host> lessPowerfulHosts = new HashSet<Host>();
        lessPowerfulHosts.addAll(getHostList());
        lessPowerfulHosts.removeAll(morePowerfulHosts);

        Map<Host, Double> hostAvailableMips = new HashMap<Host, Double>();
        for (Host h : lessPowerfulHosts)
            hostAvailableMips.put(h, h.getAvailableMips());

        for (Host h : lessPowerfulHosts) {
            List<Vm> removeVMs = new ArrayList<Vm>();
            for (Vm v : unAllocatedVMs) {
                if (hostAvailableMips.get(h) > v.getMips()) {
                    Map<String, Object> m1 = new HashMap<String, Object>();
                    m1.put("vm", v);
                    m1.put("host", h);
                    map.add(m1);
                    removeVMs.add(v);
                    hostAvailableMips.put(h, hostAvailableMips.get(h) - v.getMips());
                }

            }
            unAllocatedVMs.removeAll(removeVMs);
        }
        return map;
    }
}
