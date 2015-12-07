package fotostarana.fotostarana.unit;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import fotostrana.ru.network.NetworkManager;
import fotostrana.ru.network.proxy.ProxyManager;

public class TestAddConnection {
	  @Test
	  public void testCreateConnection(){
		  ProxyManager mockProxyManager=spy(ProxyManager.PROXY_MANAGER);
		  when(mockProxyManager.getRandomFreeProxy()).thenReturn(null);
		  NetworkManager.NETWORK_MANAGER.addNewConnection(mockProxyManager);
		  verify(mockProxyManager).updateProxy();
	  }
}
