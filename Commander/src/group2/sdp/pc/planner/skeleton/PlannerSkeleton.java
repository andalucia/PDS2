package group2.sdp.pc.planner.skeleton;

import group2.sdp.common.breadbin.DynamicPitchInfo;
import group2.sdp.pc.planner.PlanExecutor;
import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

public abstract class PlannerSkeleton implements DynamicInfoConsumer {
	
	private boolean running = false;
	
	private PlanExecutor executor;
	private ComplexCommand currentCommand;
	
	public PlannerSkeleton(PlanExecutor executor) {
		this.executor = executor;
	}
	
	public void run() {
		running = true;
	}
	
	@Override
	public void consumeInfo(DynamicPitchInfo dpi) {
		if (running) {
			boolean success = commandSuccessful(dpi);
			boolean problem = problemExists(dpi);
			if (success || problem) {
				// Wait for worker to finish. 
				ComplexCommand command = planNextCommand(dpi);
				currentCommand = command;
				executor.execute(currentCommand);
			}
		}
	}

	/**
	 * Checks if the current command succeeded, given the current pitch info.
	 * @param dpi Current pitch info.
	 * @return True if the current command is null, false otherwise. 
	 * WARNING: Override in children classes and call this method first thing.
	 */
	protected boolean commandSuccessful(DynamicPitchInfo dpi) {
		if (currentCommand == null)
			return true;
		return false;
	}
	
	/**
	 * Checks if there is a problem with executing the current command.
	 * @param dpi Current pitch info.
	 * @return True if the current command is null, false otherwise. 
	 * WARNING: Override in children classes and call this method first thing.
	 */
	protected boolean problemExists(DynamicPitchInfo dpi) {
		if (currentCommand == null)
			return true;
		return false;
	}
	
	protected abstract ComplexCommand planNextCommand(DynamicPitchInfo dpi);

}
