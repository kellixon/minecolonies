package com.minecolonies.coremod.colony.requestsystem.resolvers;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.IRequestManager;
import com.minecolonies.api.colony.requestsystem.RequestState;
import com.minecolonies.api.colony.requestsystem.location.ILocation;
import com.minecolonies.api.colony.requestsystem.request.IRequest;
import com.minecolonies.api.colony.requestsystem.requestable.IDeliverable;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.coremod.colony.buildings.AbstractBuilding;
import com.minecolonies.coremod.colony.requestsystem.requesters.BuildingBasedRequester;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ------------ Class not Documented ------------
 */
public class BuildingRequestResolver extends AbstractRequestResolver<IDeliverable>
{

    public BuildingRequestResolver(
                                    @NotNull final ILocation location,
                                    @NotNull final IToken token)
    {
        super(location, token);
    }

    @Override
    public int getPriority()
    {
        return CONST_DEFAULT_RESOLVER_PRIORITY + 100;
    }

    @Override
    public TypeToken<? extends IDeliverable> getRequestType()
    {
        return TypeToken.of(IDeliverable.class);
    }

    @Override
    public boolean canResolve(
                               @NotNull final IRequestManager manager, final IRequest<? extends IDeliverable> requestToCheck)
    {
        if (manager.getColony().getWorld().isRemote)
            return false;

        if (!(requestToCheck.getRequester() instanceof BuildingBasedRequester))
            return false;

        AbstractBuilding building = ((BuildingBasedRequester) requestToCheck.getRequester()).getBuilding();

        List<TileEntity> tileEntities = new ArrayList<>();
        tileEntities.add(building.getTileEntity());
        tileEntities.addAll(building.getAdditionalCountainers().stream().map(manager.getColony().getWorld()::getTileEntity).collect(Collectors.toSet()));

        return tileEntities.stream().map(tileEntity -> InventoryUtils.filterProvider(tileEntity, requestToCheck.getRequest()::matches)).anyMatch(itemStacks -> !itemStacks.isEmpty());
    }

    @Nullable
    @Override
    public List<IToken> attemptResolve(
                                        @NotNull final IRequestManager manager, @NotNull final IRequest<? extends IDeliverable> request)
    {
        if (canResolve(manager, request))
            return Lists.newArrayList();

        return null;
    }

    @Nullable
    @Override
    public void resolve(
                         @NotNull final IRequestManager manager, @NotNull final IRequest<? extends IDeliverable> request) throws RuntimeException
    {
        manager.updateRequestState(request.getToken(), RequestState.COMPLETED);
    }

    @Nullable
    @Override
    public IRequest getFollowupRequestForCompletion(
                                                     @NotNull final IRequestManager manager, @NotNull final IRequest<? extends IDeliverable> completedRequest)
    {
        return null;
    }

    @Nullable
    @Override
    public IRequest onParentCancelled(
                                       @NotNull final IRequestManager manager, @NotNull final IRequest<? extends IDeliverable> request) throws IllegalArgumentException
    {
        return null;
    }

    @Nullable
    @Override
    public void onResolvingOverruled(
                                      @NotNull final IRequestManager manager, @NotNull final IRequest<? extends IDeliverable> request) throws IllegalArgumentException
    {

    }

    @NotNull
    @Override
    public void onRequestComplete(@NotNull final IToken token)
    {

    }
}
