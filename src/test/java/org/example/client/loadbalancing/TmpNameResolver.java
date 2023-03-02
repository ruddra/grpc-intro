package org.example.client.loadbalancing;

import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;

import java.util.List;

public class TmpNameResolver extends NameResolver {

    private final String service;

    public TmpNameResolver(String service) {
        this.service = service;
    }

    @Override
    public String getServiceAuthority() {
        return "tmp";
    }

    @Override
    public void start(Listener2 listener){
        List<EquivalentAddressGroup> addressGroupList = ServiceRegistry.getInstances(this.service);
        ResolutionResult resolutionResult = ResolutionResult.newBuilder().setAddresses(addressGroupList).build();
        listener.onResult(resolutionResult);
    }


    @Override
    public void refresh() {
        super.refresh();
    }

    @Override
    public void shutdown() {

    }
}
