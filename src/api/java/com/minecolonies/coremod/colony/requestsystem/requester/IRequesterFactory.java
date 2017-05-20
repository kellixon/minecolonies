package com.minecolonies.coremod.colony.requestsystem.requester;

import com.minecolonies.coremod.colony.requestsystem.factory.IFactory;

/**
 * Interface describing objects that can construct IRequester objects.
 */
public interface IRequesterFactory<Input, Output extends IRequester> extends IFactory<Input, Output>
{

}
