
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * 0.1��дһ���ͻ��˽���ChatClient�����ú�λ�á���С
0.2���ڽ����������ʾ���ֵ�������������ֵ����򣬲���ӵ������У����ú�λ��
0.3��Ϊ������ӹر��¼�
0.4*�����������¼���������ȡ����������֣�������ʾ����������򣬲����������Ϊ��
0.5��дһ�������ChatServer������ʹ��serversocket���ӡ��ȴ����տͻ��˵�����accept	������

0.6���ͻ��������׽���socket�������ӣ�new����
0.7����Ҫ����������ݷ��͵��������ˣ�����DataOutputStream���ķ�ʽ��ȡ�ͻ��˵�����
	���Ӧ���ڷ���������Ҫʹ��DataInputStream������д�뵽�������ˣ����Խ��ܵ�
	�ͻ��˷�����һ�����ݡ�
0.8���������Ӧ�ͻ��˵��������ӣ����õ�����ѭ�������Խ��ܿͻ��˷����Ķ�������
0.9������쳣���⣬�����õ��û����飬����Eof�����쳣��������
1.0������ʵ�ֶ�ͻ��˵����ӣ����ҷ������˿��ԶԸ����ͻ���������Ӧ�����õĶ��̣߳�
	ÿһ���ͻ�����һ���̣߳����ھ�̬��main����ʹ���ڲ�������⣬����дһ����	�������װ��������ʹ�þ�̬�ķ������á�
 * 
 * 
 * @author yuan
 *
 */

public class ChatServer {
	boolean started = false;
	ServerSocket ss = null;
	//���ͻ������ӱ�������
	List<Client> clients= new ArrayList<Client>();
	
	
	public static void main(String[] args) {
		new ChatServer().start();
	}

	
	public void start() {

		try {
			ss = new ServerSocket(8888);
			started = true;
		} catch (BindException e) {
			System.out.println("�˿�ʹ����");
			System.out.println("��ر���س��򣬲��������з�����");
			System.exit(0);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		try {
			
			while (started) {
				Socket s = ss.accept();//�����Եķ���
				Client c= new Client(s);
System.out.println("a client connected");
				new Thread(c).start();
				clients.add(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	class Client implements Runnable{

		private Socket s;
		private DataInputStream dis=null;
		private boolean bConnected=false;
		private DataOutputStream dos=null;
		
		
		public Client(Socket s) {
			// TODO Auto-generated constructor stub
			this.s=s;
			try {
				dis=new DataInputStream(s.getInputStream());
				dos= new DataOutputStream(s.getOutputStream());
				bConnected=true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void send(String string){
			
				try {
					dos.writeUTF(string);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					clients.remove(this);
System.out.println("�Է��˳��ˣ��Ҵ�list����ȥ����");
					//e.printStackTrace();
				}
		}
		
		@Override
		public void run() {
			
			try {
				while (bConnected) {
					String str = dis.readUTF();//�����Եĺ���
System.out.println(str);
					for(int i=0;i<clients.size();i++) {
						Client c= clients.get(i);
						c.send(str);
System.out.println(" a string send");
					}
				//�ڲ�������Ч�ʲ���
//				for (Iterator<Client> iterator = clients.iterator(); iterator.hasNext();) {
//					Client client = (Client) iterator.next();
//					client.send(str);
//				}
//								
				}
				
			}catch (SocketException e) {
				// TODO: handle exception
				clients.remove(this);
				System.out.println("a client quit");
			}catch (EOFException e) {
				// TODO: handle exception
				System.out.println("client closed");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (dis != null)
						dis.close();
					if(dos!=null)
						dos.close();
					if(s!=null) {
						s.close();
						s=null;
					}
						
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
}
 