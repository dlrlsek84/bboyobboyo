package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import client.GUI.Lobby;

import client.GUI.Pop_up;

import server.model.UserDao;
import server.model.UserDto;

public class ServerJK {

	//newwork �ڿ�
	ServerSocket server_socket;
	Socket socket;
	InputStream is;
	OutputStream os;
	DataInputStream dis;
	DataOutputStream dos;

	Vector user_vc = new Vector<>();
	Vector room_vc = new Vector<>();
	StringTokenizer st;
	boolean Roomchk = true;

	public ServerJK() 
	{
		System.out.println("���� ����");
		try {
			server_socket = new ServerSocket(7777);
		} 
		catch (IOException e) {e.printStackTrace();}

		if(server_socket!=null)
		{
			Connection();
		}	
	}
	void Connection()
	{
		Thread th = new Thread(new Runnable() {
			public void run() {
				while(true)
				{	
					try 
					{
						socket = server_socket.accept();//������ ���
						UserInfo user = new UserInfo(socket);
						user.start();
						System.out.println("Ŭ���̾�Ʈ����");
					} 
					catch (IOException e) 
					{
						System.out.println("��������߻�");
						break;
					}
				}
			}
		});
		th.start();
	}
	class UserInfo extends Thread 
	{
		UserDao dao = new UserDao();
		UserDto dto = new UserDto();
		ArrayList userinfo;
		InputStream is;
		OutputStream os;
		DataInputStream dis;
		DataOutputStream dos;
		Socket client_socket;
		String Nickname;
		int score;
		public UserInfo(Socket soc) {

			this.client_socket = soc;
			UserNetwork();
		}
		void UserNetwork()
		{
			try {
				is=client_socket.getInputStream();
				dis=new DataInputStream(is);
				os=client_socket.getOutputStream();
				dos=new DataOutputStream(os);

			} catch (IOException e) {
				System.out.println("��Ʈ�� ����");
			}
		}
		public void run(){
			while(true)
			{
				try {
					String msg = dis.readUTF();
					Inmsg(msg);
				} catch (IOException e) {
					System.out.println("Ŭ���̾�Ʈ����");
					try {
						dos.close();
						dis.close();
						client_socket.close();
						user_vc.remove(this);
						
						Send_msg_all("UserOut/"+Nickname);
						Send_msg_all("userlistupdate/*");

					} 
					catch (IOException e1) 
					{}
					break;
				}
			}
		}
		void Inmsg(String str)//Ŭ���̾�Ʈ�κ��� ���� �޼���
		{
			st = new StringTokenizer(str, "/");
			String protocol = st.nextToken();
			String msg = st.nextToken();
			String name =null;
			if(protocol.equals("Note"))
			{
				String Msg = st.nextToken();
				if(Msg!=null)
				{
					for (int i = 0; i < user_vc.size(); i++) {
						UserInfo u  = (UserInfo)user_vc.elementAt(i);
						if(u.Nickname.equals(msg))
						{
							u.Send_msg("Note/"+Nickname+"/"+Msg);
						}
					}
				}
			}
			else if(protocol.equals("CreateRoom"))
			{

				for (int i = 0; i < room_vc.size(); i++) 
				{
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
					if(r.roomname.equals(msg))
					{
						Send_msg("CreateRoomFail/ok");
						Roomchk = false;
						break;
					}

				}
				if(Roomchk)//�� x ���� ����
				{
					RoomInfo new_room = new RoomInfo(msg, this);
					room_vc.add(new_room);
					Send_msg("CreateRoom/"+msg);
					Send_msg_all("NewRoom/"+msg);
					Send_msg_all("roomupdate/*");
				}
				Roomchk=true;

			}
			else if(protocol.equals("CreateHideRoom"))
			{
				String pw = st.nextToken();
				for (int i = 0; i < room_vc.size(); i++) 
				{
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
					if(r.roomname.equals(msg))
					{
						Send_msg("CreateRoomFail/ok");
						Roomchk = false;
						break;
					}

				}
				if(Roomchk)//�� x ���� ����
				{
					RoomInfo new_room = new RoomInfo(msg, this);
					new_room.pw=pw;
					room_vc.add(new_room);
					Send_msg("CreateRoom/"+msg);
					Send_msg_all("NewRoom/"+msg);
					Send_msg_all("roomupdate/*");
				}
				Roomchk=true;

			}
			else if(protocol.equals("LobbyChat"))
			{
				String chat = st.nextToken();
				Send_msg_all("Lobby/"+msg+"/"+chat);


			}
			else if(protocol.equals("romChat"))
			{
				String chat = st.nextToken();
				for (int i = 0; i < room_vc.size(); i++) {
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
					if(r.roomname.equals(msg))
					{
						r.BroadCast_Room(chat);
					}
				}
			}

			else if(protocol.equals("JoinRoom"))
			{
				String record = dao.record_chk(Nickname);
				for (int i = 0; i < room_vc.size(); i++) {
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
					if(r.roomname.equals(msg))
					{	
						if(r.roomUser_vc.size()<2)
						{
							if(r.pw.equals(""))
							{
								r.Add_user(this);
								Send_msg("JoinRoom/"+msg);
								r.BroadCast_Room(Nickname+"�� ���� ����="+record);
							}
							else
								Send_msg("HidenRoom/"+r.pw+"/"+msg);
						}
						else
							Send_msg("full/*");
					}
				}
			}
			else if(protocol.equals("HidenRoom"))
			{
				String record = dao.record_chk(Nickname);
				for (int i = 0; i < room_vc.size(); i++) {
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
					if(r.roomname.equals(msg))
					{
						if(r.roomUser_vc.size()<2)
						{
							r.Add_user(this);
							Send_msg("JoinRoom/"+msg);
							r.BroadCast_Room(Nickname+"�� ���� ����="+record);
						}
						else
							Send_msg("full/*");
					}
				}
			}
			else if(protocol.equals("FindRoom"))
			{
				String record = dao.record_chk(Nickname);
				for (int i = 0; i < room_vc.size(); i++) {
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
					if(r.roomname.equals(msg))
					{
						if(r.roomUser_vc.size()<2)
						{
							if(r.pw.equals(""))
							{
								Send_msg("JoinRoom/"+msg);
								r.BroadCast_Room(Nickname+"�� ���� ����="+record);
								r.Add_user(this);
								Roomchk = false;
							}
							else
							{
								Send_msg("HidenRoom/"+r.pw+"/"+msg);
								Roomchk = false;
							}
						}
						else
							Send_msg("full/*");
					}
				}
				if(Roomchk)
				{
					Send_msg("FindRoomFail/*");
					Roomchk = true;
				}
			}
			else if(protocol.equals("OutRoom"))
			{
				String id = st.nextToken();
				for (int i = 0; i < room_vc.size(); i++) {
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
					if(r.roomname.equals(msg))
					{
						
						Send_msg("OutRoom/ok");
						for (int j = 0; j < r.roomUser_vc.size(); j++) {
							UserInfo u = (UserInfo)user_vc.elementAt(j);
							if(u.Nickname.equals(id))
							{
								r.remove_user(u);
							}
						}
						if(r.roomUser_vc.size()==0)
						{
							Send_msg_all("RemoveRoom/"+r.roomname);
							Send_msg_all("roomupdate/*");
							r.remove();
							
						}
						else if(r.roomUser_vc.size()>0) r.BroadCast_Room(Nickname+"�� ����");
					}
				}
			}
			else if(protocol.equals("Login"))
			{
				String pw = st.nextToken();
				userinfo = new UserDao().login_chk(msg, pw);
				boolean chk = (boolean) userinfo.get(0);
				boolean idchk=true;
				for (int i = 0; i < user_vc.size(); i++) 
				{
					UserInfo u = (UserInfo)user_vc.elementAt(i);
					if(u.Nickname.equals(msg))
					{
						idchk=false;
						Send_msg("Fail/idexist");//���̵��ߺ�
					}
				}
				if(idchk)
				{
					if(dao.id_chk(msg)){
						if(chk)
						{
							Nickname = msg;
							System.out.println(Nickname+":����");

							for (int i = 0; i < user_vc.size(); i++) 
							{
								UserInfo u = (UserInfo)user_vc.elementAt(i);
								Send_msg("OldUser/"+u.Nickname);
							}

							for (int i = 0; i < room_vc.size(); i++) 
							{
								RoomInfo r = (RoomInfo)room_vc.elementAt(i);
								Send_msg("OldRoom/"+r.roomname);
							}
							Send_msg("roomupdate/*");
							user_vc.add(this);
							Send_msg_all("NewUser/"+Nickname);
							Send_msg_all("userlistupdate/*");
							Send_msg("login/"+msg);

						}
						else Send_msg("Fail/pwwrong");//���̵� Ʋ��
					}
					else Send_msg("Fail/idwrong");//��й�ȣƮ��
				}
			}
			else if(protocol.equals("Logout"))
			{
				System.out.println(Nickname+":�α׾ƿ�");
				Send_msg_all("UserOut/"+Nickname);
				for (int i = 0; i < user_vc.size(); i++) 
				{
					UserInfo u = (UserInfo)user_vc.elementAt(i);
					Send_msg("UserOut/"+u.Nickname);
				}
				user_vc.remove(this);
				Send_msg_all("userlistupdate/*");
			}
			else if(protocol.equals("Findid"))
			{
				String mail = st.nextToken();
				if(dao.name_chk(msg))
				{
					if(dao.find_idchk(msg,mail))
					{
						Send_msg("Findid/"+new UserDao().Result_findid(mail));
					}
					else Send_msg("Fail/mail");//������ ��ġ����
				}	
				else
					Send_msg("Fail/noname");//�̸��� ����
			}
			else if(protocol.equals("FindPW"))
			{
				Nickname=msg;
				String mail = st.nextToken();
				if(dao.id_chk(msg))
				{
					if(dao.find_pwchk(msg,mail))
					{
						name = msg;
						Send_msg("FindPW/*");
					}
					else Send_msg("Fail/mail");
				}
				else Send_msg("Fail/idwrong");
			}
			else if(protocol.equals("PWQnA"))
			{
				String an=st.nextToken();
				if(dao.Pw_QnA(msg, an, Nickname))
				{
					Send_msg("PWQnA/*");
				}
				else Send_msg("Fail/qna");

			}
			else if(protocol.equals("PWchange"))
			{
				new UserDao().Change_pw(Nickname, msg);
				Send_msg("PWchange/*");
			}
			else if(protocol.equals("Join"))
			{ 
				String pw = st.nextToken();
				String namee = st.nextToken();
				String number = st.nextToken();
				String birth = st.nextToken();
				String pw_q = st.nextToken();
				String pw_a = st.nextToken();
				String mail = st.nextToken();
				if(dao.id_chk(msg))
					Send_msg("Fail/notok");
				else 
				{
					if(dao.mail_chk(mail))
						Send_msg("Fail/mailnotok");
					else {
						dto.setId(msg);
						dto.setPw(pw);
						dto.setName(namee);
						dto.setTel(number);
						dto.setBirthStr(birth);
						dto.setPw_q(pw_q);
						dto.setPw_a(pw_a);
						dto.setEmail(mail);
						new UserDao().insert(dto);

						Send_msg("Fail/"+dao.joinres);
					}
				}
			}
			else if(protocol.equals("IDchk"))
			{
				if(dao.id_chk(msg)) Send_msg("Fail/notok");
				else Send_msg("Fail/ok");
			}
			else if(protocol.equals("Ready"))
			{
				String id = st.nextToken();
				for (int i = 0; i < room_vc.size(); i++) 
				{
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
					if(r.roomname.equals(msg)) {
						r.ready++;
						r.BroadCast_Room(id+"�� �غ�Ǿ����ϴ�.");
						if(r.ready>=2)
						{
							r.BroadCast_Game();
						}
					}

				}

			}
			else if(protocol.equals("GameResult"))
			{
				dao.GameResult(msg, Nickname);
			}
			else if(protocol.equals("Readycancel"))
			{
				String id = st.nextToken();
				for (int i = 0; i < room_vc.size(); i++) 
				{
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
					if(r.roomname.equals(msg)) {
						r.ready--;
						r.BroadCast_Room(id+"�� �غ� �����߽��ϴ�.");
					}

				}

			}
			else if(protocol.equals("Coord"))
			{
				String id = st.nextToken();
				String coord = st.nextToken();
				for (int i = 0; i < room_vc.size(); i++) 
				{
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
					if(r.roomname.equals(msg))
					{
						for (int t = 0; t < r.roomUser_vc.size(); t++) 
						{
							UserInfo u = (UserInfo)r.roomUser_vc.elementAt(t);
							if(!u.Nickname.equals(id))
								u.Send_msg("print/"+coord);
						}
					}
				}

			}
			else if(protocol.equals("Gameover"))
			{
				score = Integer.parseInt(st.nextToken());
				for (int i = 0; i < room_vc.size(); i++) 
				{
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
				    if(r.roomname.equals(msg))
				    {
				    	r.gameset++;
				    	if(r.gameset>=2)
				    	{
				    		r.BroadCast_result();
				    	}
				    }
				}
			}
				

		}
		void Send_msg(String str)
		{
			try 
			{
				dos.writeUTF(str);
			} 
			catch (IOException e) {e.printStackTrace();}
		}
		void Send_msg_all(String str)//��ü��
		{
			for (int i = 0; i < user_vc.size(); i++) 
			{
				UserInfo u = (UserInfo)user_vc.elementAt(i);
				u.Send_msg(str);
			}
		} 
	}
	class RoomInfo 
	{

		String roomname;
		String pw="";
		int ready = 0;
		int gameset = 0;
		Vector roomUser_vc = new Vector<>();
		public RoomInfo(String str,UserInfo u) 
		{
			this.roomname = str;
			this.roomUser_vc.add(u);

		}
		public void BroadCast_Room(String str)
		{
			for (int j = 0; j < roomUser_vc.size(); j++) 
			{
				UserInfo u = (UserInfo)roomUser_vc.elementAt(j);
				u.Send_msg("romChat/"+str);
			}
		}
		public void BroadCast_Game()
		{
			for (int j = 0; j < roomUser_vc.size(); j++) 
			{
				UserInfo u = (UserInfo)roomUser_vc.elementAt(j);
				u.Send_msg("Gamestart/*");

			}
			ready=0;
		}
		public void BroadCast_result()
		{
				UserInfo u1 = (UserInfo)roomUser_vc.elementAt(0);
				UserInfo u2 = (UserInfo)roomUser_vc.elementAt(1);
				if(u1.score>u2.score)
				{
					u1.Send_msg("Gameset/"+"�¸�");
					u2.Send_msg("Gameset/"+"�й�");
				}
				else
				{
					u1.Send_msg("Gameset/"+"�й�");
					u2.Send_msg("Gameset/"+"�¸�");
				}
			ready=0;
		}
		void Add_user(UserInfo u)
		{
			this.roomUser_vc.add(u);
		}
		void remove_user(UserInfo u)
		{
			this.roomUser_vc.remove(u);
		}
		void remove()
		{
			room_vc.remove(this);
		}
	}
	public static void main(String[] args) {
		new ServerJK();
	}
}
