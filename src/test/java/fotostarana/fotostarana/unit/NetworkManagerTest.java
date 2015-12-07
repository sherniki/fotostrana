package fotostarana.fotostarana.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Test;

import configuration.ApplicationConfiguration;
import fotostrana.ru.network.Connection;
import fotostrana.ru.network.NetworkManager;
public class NetworkManagerTest {

  @Test
  public void testConfiguration(){
    ApplicationConfiguration mockConfig=spy(ApplicationConfiguration.INSTANCE);
    when(mockConfig.getIntValue("configuration.Network.TimeOut")).thenReturn(10);
    when(mockConfig.getIntValue("configuration.Network.TimeSleep")).thenReturn(10);
    when(mockConfig.getIntValue("configuration.Network.TimeSleep")).thenReturn(10);
    NetworkManager manager=NetworkManager.NETWORK_MANAGER;
    manager.loadConfiguration(mockConfig);
    assertEquals(Connection.TIME_OUT, 10000);    
    assertEquals(Connection.TIME_SLEEP, 10000);    
    assertEquals(manager.MAX_COUNT_CONNECTION, 10);    
  }
}
