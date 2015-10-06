package fotostrana.ru.task.schedulers;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventOfNetworkRequests;
import fotostrana.ru.events.network.votes.EventCanNotVote;
import fotostrana.ru.events.schedulers.SchedulerEvent;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestVote;
import fotostrana.ru.task.Task;
import fotostrana.ru.task.tasks.TaskQuickVoting;

/**
 * Планировщик выполнения быстрого голосования
 * 
 */
public class SchedulerQuickTaskWithLimitedThreads extends
		SchedulerWithLimitedThreads {
	protected TaskQuickVoting taskQuickVoting;

	/**
	 * @param task
	 *            задание, выполнение которого будут планироваться
	 */
	public SchedulerQuickTaskWithLimitedThreads(TaskQuickVoting task) {
		super(task);
		taskQuickVoting = task;
	}

	/**
	 * @param task
	 *            задание, выполнение которого будут планироваться
	 * @param maxCountThreads
	 *            маскимальное количество одновременно выполняющихся запросов
	 */
	public SchedulerQuickTaskWithLimitedThreads(Task task, int maxCountThreads) {
		super(task, maxCountThreads);
	}

	@Override
	protected synchronized boolean executeOneTask() {
		boolean result = super.executeOneTask();
//		if (result) {
			countOfExecutedTasksToComplete++;
//		}
		return result;
	}

	@Override
	public synchronized void handleEvent(Event event) {
		if (!(event instanceof SchedulerEvent)) {
			return;
		}
		if (event instanceof EventOfNetworkRequests) {
			Request request = ((EventOfNetworkRequests) event).getRequest();
			RequestVote requestVote = null;
			try {
				if (request.getParentRequest() == null) {
					requestVote = (RequestVote) request;
				} else {
					requestVote = (RequestVote) request.getParentRequest();
				}
			} catch (Exception e) {
			}
			if (requestVote != null) {
				int delta = requestVote.countSuccessfulVotes
						- requestVote.countOfVotesRequired;
				taskQuickVoting.expectedCountOfVotes.addAndGet(delta);
				if (delta < 0) {
					countOfExecutedTasksToComplete--;
					event = new EventCanNotVote(requestVote);
				}
			}
		}
		super.handleEvent(event);
	}

	@Override
	protected boolean isPossibleToExecuteANewTask() {
		return (currentСount < maximumCount);
	}
}
