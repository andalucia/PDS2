package group2.sdp.pc.planner.skeleton;

import group2.sdp.pc.planner.operation.Operation;

/**
 * A class implementing this interface will consume an Operation
 * @author Alfie
 *
 */
public interface OperationConsumer {

	/**
	 * Consumes the operation
	 * @param operation The operation to be consumed
	 */
	public void consumeOperation(Operation operation);

	public void start();
	
	public void stop();
}
