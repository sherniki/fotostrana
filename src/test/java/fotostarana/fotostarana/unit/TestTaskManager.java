package fotostarana.fotostarana.unit;

import org.junit.Test;

import static org.mockito.Mockito.*;
import fotostrana.ru.task.TaskManager;
import fotostrana.ru.task.tasks.TestTask;

public class TestTaskManager {

	@Test
	public void testStartTask(){
		TestTask task=mock(TestTask.class);
		TaskManager mockTaskManager=spy(TaskManager.TASK_MANAGER);
		mockTaskManager.executeTask(task);
		verify(task).start();
		verify(mockTaskManager).addTask(task);
	}
	
	
}
