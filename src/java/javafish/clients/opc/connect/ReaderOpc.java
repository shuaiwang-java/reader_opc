package javafish.clients.opc.connect;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafish.clients.opc.JOpc;
import javafish.clients.opc.SynchReadItemExample;
import javafish.clients.opc.component.OpcGroup;
import javafish.clients.opc.component.OpcItem;
import javafish.clients.opc.exception.ComponentNotFoundException;
import javafish.clients.opc.exception.ConnectivityException;
import javafish.clients.opc.exception.SynchReadException;
import javafish.clients.opc.exception.UnableAddGroupException;
import javafish.clients.opc.exception.UnableAddItemException;
/**
 * 读取opc服务端
 * @author Administrator
 * @param <V>
 */
public class ReaderOpc {
	
	private static final Logger logger = LoggerFactory.getLogger(ReaderOpc.class);
	
	public static void main(String[] args) {
		
		SynchReadItemExample test = new SynchReadItemExample();
		JOpc.coInitialize();   //初始化JOpc        JOpc继承父类JCustomOpc
		
		/**
		 * 创建连接对象
		 * 127.0.0.1  				 opcService的地址,opcService在本地就填写本地地址
		 * KingView.View.1     opcService在系统注册表中的注册名,  组态王中的opcService默认名KingView.View.1,如果你不是使用组态王中的opcService那么请修改你要连接的注册名(opcService服务名称)
		 * JOPC1						OPC客户端的用户名---按你喜欢来填
		 */
		JOpc jopc = new JOpc("127.0.0.1", "KingView.View.1", "JOPC1");
		
		OpcGroup group = new OpcGroup("Group", true, 500, 0.0f);
		
		group.addItem(new OpcItem("K1.Value", true, ""));   // new Opcitem("K1.Value",true,"");    "K1.Value"  表示要读取opc服务器中的变量名称的值。   
		group.addItem(new OpcItem("K2.Value", true, ""));
		group.addItem(new OpcItem("K3.Value", true, ""));
		
		jopc.addGroup(group);   //添加组
		OpcGroup responseGroup;

		try {
			jopc.connect();   //连接
			jopc.registerGroups();  //注册组
		} catch (ConnectivityException e1) {
			logger.error(e1.getMessage());
		} catch (UnableAddGroupException e) {
			logger.error(e.getMessage());
		} catch (UnableAddItemException e) {
			logger.error(e.getMessage());
		}
		synchronized(test) {
			try {
				test.wait(50);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}
		//同步读取
		while (true) {
			try {
				synchronized(test) {
					test.wait(5000);
				}
				responseGroup = jopc.synchReadGroup(group);
				ArrayList<OpcItem> opcItems = responseGroup.getItems();
				for (OpcItem opcItem : opcItems) {
					System.out.println("Item名:" + opcItem.getItemName()  +  "  Item值: " + opcItem.getValue());
				}
			} catch (ComponentNotFoundException e) {
				logger.error(e.getMessage()); //获取responseGroup错误
				JOpc.coUninitialize();     //错误关闭连接
			}catch (InterruptedException e) {
				logger.error(e.getMessage());
				JOpc.coUninitialize(); //错误关闭连接
			} catch (SynchReadException e) {
				e.printStackTrace();
			}
		}
	}
}


































































