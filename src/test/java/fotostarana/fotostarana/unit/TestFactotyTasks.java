package fotostarana.fotostarana.unit;

import static org.junit.Assert.*;

import org.junit.Test;

import fotostrana.ru.task.FactoryTasks;
import fotostrana.ru.task.Task;
import fotostrana.ru.task.tasks.nominations.TaskVotingInTheNomination;

public class TestFactotyTasks {

	@Test
	public void testIncorrectInputs() {
		assertEquals(FactoryTasks.createTask(null), null);
		assertEquals(FactoryTasks.createTask(""), null);
		assertEquals(FactoryTasks.createTask("|||"), null);
		assertEquals(FactoryTasks.createTask("1|2|3|4"), null);
	}

	@Test
	public void testNominationsTask() {
		Task task = FactoryTasks
				.createTask(" имя задачи| номинация | 123458234|12");
		assertNotEquals(task, null);
		assertTrue(task instanceof TaskVotingInTheNomination);
		TaskVotingInTheNomination nominationTask=(TaskVotingInTheNomination) task;
		assertEquals(nominationTask.getTargetId(), "123458234");
		assertEquals(nominationTask.getTargetName(), "имязадачи");
		assertEquals(nominationTask.getCountVotes(), 12);
	}
}
