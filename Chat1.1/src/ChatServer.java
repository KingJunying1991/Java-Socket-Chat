
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


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
		
		public void send(String string) {
			try {
				dos.writeUTF(string);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
					}
				//�ڲ�������Ч�ʲ���
//				for (Iterator<Client> iterator = clients.iterator(); iterator.hasNext();) {
//					Client client = (Client) iterator.next();
//					client.send(str);
//				}
//				
						
				}
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
					if(s!=null)
						s.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
}
 