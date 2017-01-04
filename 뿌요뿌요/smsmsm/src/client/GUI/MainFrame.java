package client.GUI;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class MainFrame extends JFrame implements ActionListener
{

	CardLayout card = new CardLayout();
	Lobby lb = new Lobby();
	Login lg = new Login();
	Room ro = new Room();
	JPanel p1 = lb.lobby;
	JPanel p2 = lg.login;
	JPanel p3 = ro.room;
	JButton login_btn = new JButton("Login");
	JButton send = new JButton("����");
	JButton whisper = new JButton("�Ӹ�");
	JButton crRom = new JButton("�游���");
	JButton fiRom = new JButton("��ã��");
	JButton joRom = new JButton("������");
	JButton join_btn = new JButton("Join");
	JButton find_ID_btn = new JButton("Find ID");
	JButton find_PW_btn = new JButton("Find PW");
	JButton start = new JButton("�غ�");
	JButton cancel = new JButton("�غ�����");
	JButton out = new JButton("������");
	JButton logout = new JButton("�α׾ƿ�");
	Find_PW pw;
	PW_QnA qna;
	PW_Change change;
	

	//net res
	Socket socket;
	InputStream is;
	OutputStream os;
	DataInputStream dis;
	DataOutputStream dos;
	StringTokenizer st;
	Sender sd;
	Game_Room ga;
	boolean res = false;
	Vector userlist = new Vector<>();
	Vector roomlist = new Vector<>();


	ArrayList userinfo;//���������� ������ �ִ� ����Ʈ
	String myrom;//�� ���� ��
	String myid;//���� �� ���̵�

	public MainFrame() {

		connect();
		setTitle("�����̻ѳ�:�״뿡�� ��ġ�� ��������");
		setLayout(card);
		setBounds(10,20, 920, 690);
		add(p1,"�κ�");
		add(p2,"�α���");
		add(p3,"���ӹ�");
		login_btn.setBounds(500, 500, 100, 40);
		login_btn.setBackground(Color.GRAY);
		p2.add(login_btn);
		join_btn.setBounds(500, 550, 100, 40);
		join_btn.setBackground(Color.GRAY);
		join_btn.addActionListener(this);
		p2.add(join_btn);
		login_btn.addActionListener(this);
		send.setBounds(780,560, 70, 30);
		p1.add(send);
		whisper.setBounds(780,240, 70, 30);
		p1.add(whisper);
		crRom.setBounds(50, 560, 100, 30);
		p1.add(crRom);
		crRom.addActionListener(this);
		fiRom.setBounds(150, 560, 100, 30);
		fiRom.addActionListener(this);
		p1.add(fiRom);
		joRom.setBounds(250, 560, 100, 30);
		joRom.addActionListener(this);
		p1.add(joRom);
		find_ID_btn.setBounds(300, 550, 100, 40);
		find_ID_btn.setBackground(Color.GRAY);
		find_ID_btn.addActionListener(this);
		p2.add(find_ID_btn);
		find_PW_btn.setBounds(400, 550, 100, 40);
		find_PW_btn.setBackground(Color.GRAY);
		find_PW_btn.addActionListener(this);
		p2.add(find_PW_btn);
		whisper.addActionListener(this);
		send.addActionListener(this);
		lb.chat.addActionListener(this);
		lg.pw_txt.addActionListener(this);
		card.show(getContentPane(), "�α���");
		lb.room.setListData(roomlist);

		start.setBounds(410, 500, 100, 60);
		p3.add(start);
		out.setBounds(510, 500, 100, 60);
		p3.add(out);
		cancel.setBounds(310, 500, 100, 60);
		cancel.setEnabled(false);
		p3.add(cancel);
		logout.setBounds(750, 10, 100, 30);
		p1.add(logout);
		logout.addActionListener(this);
		cancel.addActionListener(this);
		start.addActionListener(this);
		out.addActionListener(this);
		ro.chat.addActionListener(this);
		addWindowListener(new WinClose());
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	void connect(){
		try 
		{
			socket = new Socket("192.168.30.135", 7777);
			is=socket.getInputStream();
			dis=new DataInputStream(is);
			os=socket.getOutputStream();
			dos=new DataOutputStream(os);
			sd = new Sender(socket);
		}
		catch (UnknownHostException e)
		{
			new Pop_up("�������");
		}
		catch (IOException e) 
		{
			try {
				os.close();
				is.close();
				dos.close();
				dis.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				new Pop_up("�������");
				e1.printStackTrace();
			} finally{
				new Pop_up("�������");
			}


		}
		Thread th = new Thread(new Runnable() {
			public void run() {
				while(true)
				{
					try 
					{
						String readmsg=dis.readUTF();
						inmsg(readmsg);
					} 
					catch (IOException e) 
					{
						try {
							os.close();
							is.close();
							dos.close();
							dis.close();
							socket.close();
							new Pop_up("�������");
						} catch (IOException e1){}
						break;
					}	
				}

			}
		});
		th.start();

	}
	void inmsg(String str)
	{
		st= new StringTokenizer(str, "/");
		String protocol = st.nextToken();
		String msg = st.nextToken();
		if(protocol.equals("NewUser"))
		{
			userlist.add(msg);
		}
		else if(protocol.equals("OldUser"))
		{
			userlist.add(msg);
		}
		else if(protocol.equals("Note"))
		{
			String MMsg = st.nextToken();
			lb.chatview.append(msg+"�κ��� �ӼӸ�:"+MMsg+"\n");
		}
		else if(protocol.equals("userlistupdate"))
		{
			lb.user.setListData(userlist);
		}
		else if(protocol.equals("roomupdate"))
		{
			lb.room.setListData(roomlist);
		}
		else if(protocol.equals("CreateRoom"))
		{
			myrom = msg;
			card.show(getContentPane(),"���ӹ�");
		}
		else if(protocol.equals("CreateRoomFail"))
		{
			new Pop_up("�̹� ���� ����");
		}
		else if(protocol.equals("FindRoomFail"))
		{
			new Pop_up("���� ����!");
		}
		else if(protocol.equals("NewRoom"))
		{
			roomlist.add(msg);
		}
		else if(protocol.equals("OldRoom"))
		{
			roomlist.add(msg);
		}
		else if(protocol.equals("Lobby"))
		{
			String note = st.nextToken();
			lb.chatview.append(msg+":"+note+"\n");
			lb.chJS.getVerticalScrollBar().setValue(lb.chJS.getVerticalScrollBar().getMaximum());
		}
		else if(protocol.equals("romChat"))
		{
			ro.roomChat.append(msg+"\n");
			ro.rcJs.getVerticalScrollBar().setValue(ro.rcJs.getVerticalScrollBar().getMaximum());
		}
		else if(protocol.equals("JoinRoom"))
		{
			myrom = msg;
			card.show(getContentPane(),"���ӹ�");
		}
		else if(protocol.equals("HidenRoom"))
		{
			String pw = JOptionPane.showInputDialog("��й�ȣ");
			String name = st.nextToken();
			if(pw.equals(msg))
				sd.send_msg("HidenRoom/"+name);
			else new Pop_up("��й�ȣ�� ���� �ʽ��ϴ�.");
		}
		else if(protocol.equals("UserOut"))
		{
			userlist.remove(msg);
		}
		else if(protocol.equals("OutRoom"))
		{
			ro.roomChat.setText("");
			card.show(getContentPane(),"�κ�");
		}
		else if(protocol.equals("RemoveRoom"))
		{
			roomlist.remove(msg);
		}
		else if(protocol.equals("full"))
		{
			new Pop_up("���ο� �ʰ�");
		}
		else if(protocol.equals("login"))
		{
			card.show(getContentPane(), "�κ�");
		}
		else if(protocol.equals("Logout"))
		{
			card.show(getContentPane(), "�α���");
		}
		else if(protocol.equals("Findid"))
		{
			new Pop_up(msg);
		}
		else if(protocol.equals("FindPW"))
		{
			new PW_QnA();
		}
		else if(protocol.equals("PWQnA"))
		{
			new PW_Change();
		}
		else if(protocol.equals("PWchange"))
		{
			new Pop_up("��й�ȣ ����");
		}
		else if(protocol.equals("print"))
		{
			st = new StringTokenizer(msg,"&,");

			while(st.hasMoreElements())
			{
				ga.myPanel_2.test(st.nextToken(),st.nextToken(),st.nextToken());
			}
		}
		else if(protocol.equals("Gamestart"))
		{
			start.setEnabled(true);
			cancel.setEnabled(false);
			ga = new Game_Room(socket);
		}
		else if(protocol.equals("Gameset"))
		{
			String res = "record_v";
			new Pop_up(msg);
			if(msg.equals("�¸�"))
				sd.send_msg("GameResult/"+res);
			else
				sd.send_msg("GameResult/"+"record_d");
		}
		else if(protocol.equals("Fail"))
		{
			if(msg.equals("idexist")) new Pop_up("�������� ���̵�");
			else if(msg.equals("idwrong")) new Pop_up("ID�� Ʋ�Ƚ��ϴ�");
			else if(msg.equals("pwwrong")) new Pop_up("��й�ȣ�� Ʋ�Ƚ��ϴ�");
			else if(msg.equals("mail")) new Pop_up("�������� �ʴ� ����");
			else if(msg.equals("noname")) new Pop_up("�������� �ʴ� �̸�");
			else if(msg.equals("qna")) new Pop_up("����/�亯�� Ȯ���ϼ���");
			else if(msg.equals("chk")) new Pop_up("��й�ȣ�� Ȯ���ϼ���");
			else if(msg.equals("ok")) new Pop_up("���̵� ��밡��");
			else if(msg.equals("notok")) new Pop_up("������� ���̵�");
			else if(msg.equals("complete")) new Pop_up("ȸ������ �Ϸ�");
			else if(msg.equals("mailnotok")) new Pop_up("������� �̸���");
		}

	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==login_btn)
		{
			if(lg.id_txt.getText().equals("")||lg.id_txt.getText().equals(null))
				new Pop_up("���̵� �Է��ϼ���");
			else
			{
				if(lg.pw_txt.getText().equals("")||lg.pw_txt.getText().equals(null))
					new Pop_up("��й�ȣ�� �Է��ϼ���");
				else{
					sd.send_msg("Login/"+lg.id_txt.getText()+"/"+lg.pw_txt.getText());
					myid = lg.id_txt.getText();
				}
			}
		}
		else if(e.getSource()==send)
		{
			if(lb.chat.getText().equals(null)||lb.chat.getText().equals("")||lb.chat.getText().equals("/")){
				new Pop_up("������ �Է��ϼ���");
			}
			else sd.send_msg("LobbyChat/"+lg.id_txt.getText().trim()+"/"+lb.chat.getText().trim());
			lb.chat.setText("");
			lb.chJS.getVerticalScrollBar().setValue(lb.chJS.getVerticalScrollBar().getMaximum());
		}
		else if(e.getSource()==lb.chat)
		{
			if(lb.chat.getText().equals(null)||lb.chat.getText().equals("")||lb.chat.getText().equals("/")){
				new Pop_up("������ �Է��ϼ���");
			}
			else sd.send_msg("LobbyChat/"+lg.id_txt.getText().trim()+"/"+lb.chat.getText().trim());
			lb.chat.setText("");

		}
		else if(e.getSource()==whisper)
		{
			String user = (String)lb.user.getSelectedValue();
			String note = JOptionPane.showInputDialog("�����޼���");

			if(note.equals("")||note.equals(null))
				new Pop_up("�޼����� �Է��ϼ���");
			else 
			{
				sd.send_msg("Note/"+user+"/"+note);
				lb.chatview.append(user+"���� ���� �ӼӸ�:"+note+"\n");
			}
		}
		else if(e.getSource()==crRom)
		{
			new Room_Create();
		}
		else if(e.getSource()==fiRom)
		{
			new Room_Find();
		}
		else if(e.getSource()==joRom)
		{
			String JoinRoom =(String)lb.room.getSelectedValue();
			sd.send_msg("JoinRoom/"+JoinRoom);

		}
		else if(e.getSource()==join_btn)
		{
			new Join();
		}
		else if(e.getSource()==find_ID_btn)
		{
			new Find_ID();
		}
		else if(e.getSource()==find_PW_btn)
		{
			new Find_PW();
		}
		else if(e.getSource()==start)
		{	
			start.setEnabled(false);
			cancel.setEnabled(true);

			sd.send_msg("Ready/"+myrom+"/"+myid);
		}
		else if(e.getSource()==cancel)
		{
			cancel.setEnabled(false);
			start.setEnabled(true);
			sd.send_msg("Readycancel/"+myrom+"/"+myid);
		}
		else if(e.getSource()==out)
		{
			sd.send_msg("OutRoom/"+myrom+"/"+myid);
		}
		else if(e.getSource()==ro.chat)
		{
			if(ro.chat.getText().equals(null)||ro.chat.getText().equals("")||ro.chat.getText().equals("/")){
				new Pop_up("������ �Է��ϼ���");
			}
			else 
			{
				sd.send_msg("romChat/"+myrom+"/"+myid+":"+ro.chat.getText());
				ro.chat.setText("");
				ro.rcJs.getVerticalScrollBar().setValue(ro.rcJs.getVerticalScrollBar().getMaximum());
			}
		}
		else if(e.getSource()==logout)
		{
			lb.chatview.setText("");
			card.show(getContentPane(), "�α���");
			sd.send_msg("Logout/"+myid);

		}
	}
	class WinClose extends WindowAdapter{
		@Override
		public void windowClosing(WindowEvent e) {
			// TODO Auto-generated method stub
			System.out.println("������");
			sd.send_msg("OutRoom/"+myrom+"/"+myid);
			
		}
	}
	public class Join extends JFrame implements ActionListener {

		Calendar today = Calendar.getInstance();
		JTextField id = new JTextField();
		JPasswordField pw = new JPasswordField();
		JPasswordField pwchk = new JPasswordField();
		JTextField name = new JTextField();
		JLabel num2 = new JLabel("-");
		JTextField number2 = new JTextField();
		JLabel num3 = new JLabel("-");
		JTextField number3 = new JTextField();
		JTextField emailAddress = new JTextField();
		JTextField emailAddress2 = new JTextField();
		JButton b1_1;
		JButton b10;
		JComboBox number,email, yy, mm, dd, quiz;
		JTextField Answer = new JTextField();
		public Join(){
			setTitle("Join SeyoungPuNE");
			setBounds(101, 200, 530, 380);
			setLayout(null);
			id.setBounds(101,  10, 100, 30);
			add(id);
			pw.setBounds(101,  40, 100, 30);
			add(pw);
			pwchk.setBounds(101,  70, 100, 30);
			add(pwchk);
			name.setBounds(101,  100, 100, 30);
			add(name);
			Vector<String> numberArr = new Vector<>();
			numberArr.add("010");
			numberArr.add("011");
			numberArr.add("016");
			numberArr.add("017");
			numberArr.add("019");
			number = new JComboBox<>(numberArr);
			number.setBounds(101,  131, 60, 30);
			add(number);
			num2.setBounds(166, 131, 10, 30);
			add(num2);
			number2.setBounds(176,  131, 60, 30);
			add(number2);
			num3.setBounds(241, 131, 10, 30);
			add(num3);
			number3.setBounds(251,  131, 60, 30);
			add(number3);
			Vector<String> yyArr = new Vector<>();
			for (int i = 1970; i <= today.get(Calendar.YEAR) ; i++) {
				yyArr.add(""+i);
			}
			yy = new JComboBox<>(yyArr);
			yy.setBounds(101, 161, 60, 30);
			add(yy);
			JLabel year = new JLabel("��");
			year.setBounds(166, 161, 20, 30);
			add(year);
			Vector<String> mmArr = new Vector<>();
			for (int i = 1; i <= today.getActualMaximum(Calendar.MONTH)+1; i++) {
				if(i<10) mmArr.add("0"+i);
				else mmArr.add(""+i);
			}
			mm = new JComboBox<>(mmArr);
			mm.setBounds(181, 161, 50, 30);
			add(mm);
			JLabel month = new JLabel("��");
			month.setBounds(236, 161, 20, 30);
			add(month);
			Vector<String> ddArr = new Vector<>();
			for (int i = 1; i <= today.getActualMaximum(Calendar.DATE) ; i++) {
				if(i<10) ddArr.add("0"+i);
				else ddArr.add(""+i);
			}
			dd = new JComboBox<>(ddArr);
			dd.setBounds(251, 161, 50, 30);
			add(dd);
			JLabel date = new JLabel("��");
			date.setBounds(306, 161, 20, 30);
			add(date);
			emailAddress.setBounds(101, 191, 70, 30);
			add(emailAddress);
			JLabel mail = new JLabel("@");
			mail.setBounds(176, 191, 20, 30);
			add(mail);
			Vector<String> emailArr = new Vector<>();
			emailArr.add("daum.net");
			emailArr.add("naver.com");
			emailArr.add("google.com");
			emailArr.add("�����Է�");
			email = new JComboBox<>(emailArr);
			email.setBounds(196, 191, 70, 30);
			add(email);
			emailAddress2.setBounds(276, 191, 70, 30);
			add(emailAddress2);
			Vector<String> quizArr = new Vector<>();
			quizArr.add("�� ������?");
			quizArr.add("�� ���� 1ȣ��?");
			quizArr.add("�� ��� �б���?");
			quizArr.add("�� ��� ������?");
			quiz = new JComboBox(quizArr);
			quiz.setBounds(101,221,400,30);
			add(quiz);
			Answer.setBounds(101, 251, 400, 30);
			add(Answer);
			JButton b1 = new JButton("���̵�");
			b1.setBackground(Color.gray);
			b1.setEnabled(false);
			b1.setBounds(0, 10, 100, 30);
			add(b1);
			b1_1 = new JButton("�ߺ�üũ");
			b1_1.setBackground(Color.gray);
			b1_1.setBounds(202, 10, 100, 30);
			add(b1_1);
			b1_1.addActionListener(this);
			JButton b2 = new JButton("��й�ȣ");
			b2.setBackground(Color.gray);
			b2.setEnabled(false);
			b2.setBounds(0, 40, 100, 30);
			add(b2);
			JButton b3 = new JButton("���Ȯ��");
			b3.setBackground(Color.gray);
			b3.setEnabled(false);
			b3.setBounds(0, 70, 100, 30);
			add(b3);
			JButton b4 = new JButton("�̸�");
			b4.setBackground(Color.gray);
			b4.setEnabled(false);
			b4.setBounds(0, 100, 100, 30);
			add(b4);
			JButton b5 = new JButton("��ȭ��ȣ");
			b5.setBackground(Color.gray);
			b5.setEnabled(false);
			b5.setBounds(0, 130, 100, 30);
			add(b5);
			JButton b6 = new JButton("�������");
			b6.setBackground(Color.gray);
			b6.setEnabled(false);
			b6.setBounds(0, 160, 100, 30);
			add(b6);
			JButton b7 = new JButton("�̸���");
			b7.setBackground(Color.gray);
			b7.setEnabled(false);
			b7.setBounds(0, 190, 100, 30);
			add(b7);
			JButton b8 = new JButton("����");
			b8.setBackground(Color.gray);
			b8.setEnabled(false);
			b8.setBounds(0, 220, 100, 30);
			add(b8);
			JButton b9 = new JButton("�亯");
			b9.setBackground(Color.gray);
			b9.setEnabled(false);
			b9.setBounds(0, 250, 100, 30);
			add(b9);
			b10 = new JButton("����");
			b10.setBackground(Color.gray);
			b10.setBounds(170, 300, 100, 30);
			add(b10);
			b10.addActionListener(this);
			JButton b11 = new JButton("���");
			b11.setBackground(Color.gray);
			b11.setBounds(270, 300, 100, 30);
			add(b11);

			b11.addActionListener(new Cancel(this));
			setVisible(true);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(e.getSource()==b10)
			{
				boolean mailchk = false;
				String mail = null;
				if(email.getSelectedItem().equals("�����Է�")){
					if(emailAddress2.getText().equals("")||emailAddress2.getText().equals(null))
						new Pop_up("������ �Է��ϼ���");
					else{
						mail=emailAddress.getText()+"@"+emailAddress2.getText();
						mailchk=true;
					}

				}
				else {
					mail=emailAddress.getText()+"@"+email.getSelectedItem();
					mailchk=true;
				}
				if(mailchk)
				{
					if(id.getText().equals("")) new Pop_up("ID�� Ȯ���ϼ���.");
					else if(pw.getText().equals("")) new Pop_up("PW�� �Է��ϼ���.");
					else if(pwchk.getText().equals("")) new Pop_up("PWȮ���� �Է��ϼ���.");
					else if(name.getText().equals("")) new Pop_up("�̸��� Ȯ���ϼ���.");
					else if(emailAddress.getText().equals("")) new Pop_up("�̸����� Ȯ�����ּ���.");
					else if(Answer.getText().equals("")) new Pop_up("������ �亯�� Ȯ���ϼ���.");
					else if(pw.getText().equals(pwchk.getText()))
					{						
						sd.send_msg("Join/"+id.getText()+
								"/"+pw.getText()+"/"+
								name.getText()+"/"+
								number.getSelectedItem()+"-"+number2.getText()+"-"+number3.getText()+"/"+
								yy.getSelectedItem()+"-"+mm.getSelectedItem()+"-"+dd.getSelectedItem()+"/"+
								quiz.getSelectedItem()+"/"+
								Answer.getText()+"/"+
								mail);
					}
					else new Pop_up("PW�� PWȮ�� ��ġ���� �ʽ��ϴ�.");
				}

			}
			if(e.getSource()==b1_1) 
			{
				if(id.getText().equals("")) new Pop_up("ID�� Ȯ���ϼ���.");
				else
				{
					sd.send_msg("IDchk/"+id.getText());
				}

			}

		}
	}
	class Room_Create extends JFrame implements ActionListener
	{
		JLabel title = new JLabel("�� �� ��");
		JTextField tiTF = new JTextField();
		JCheckBox hide = new JCheckBox("�������");
		JPasswordField hiTF= new JPasswordField();
		JButton create = new JButton("�����");
		JButton cancel = new JButton("���");

		public Room_Create() {
			setTitle("�游���");
			setBounds(10,20,300,200);
			setLayout(null);
			title.setBounds(30, 25, 50, 30);
			add(title);
			tiTF.setBounds(110, 25, 120, 30);
			add(tiTF);
			hide.setBounds(30, 65, 80, 30);
			add(hide);
			hiTF.setBounds(110, 65, 120, 30);
			add(hiTF);
			create.setBounds(50, 105, 80, 30);
			add(create);
			hide.addActionListener(this);
			hiTF.setEditable(false);
			create.addActionListener(this);
			cancel.setBounds(140, 105, 80, 30);
			add(cancel);
			cancel.addActionListener(new Cancel(this));
			setVisible(true);	
		}
		@Override
		public void actionPerformed(ActionEvent e) {

			if(e.getSource()==create)
			{

				if(tiTF.getText().equals(null)||tiTF.getText().equals(""))
					new Pop_up("���̸��� �Է��ϼ���");
				else{
					if(hide.isSelected())
					{
						if(hiTF.getText().equals(""))
							new Pop_up("��й�ȣ�� �Է��ϼ���");
						else
						{ 	
							sd.send_msg("CreateHideRoom/"+tiTF.getText()+"/"+hiTF.getText());
							dispose();
						}
					}
					else 
					{
						sd.send_msg("CreateRoom/"+tiTF.getText());
						dispose();
					}
				}
			}
			else if(e.getSource()==hide)
			{
				if(hide.isSelected())
				{
					hiTF.setEditable(true);
					hiTF.setText("");
				}
				else 
				{
					hiTF.setEditable(false);
					hiTF.setText("");
				}
			}

		}
	}
	class Room_Find extends JFrame implements ActionListener
	{
		JLabel roomNum = new JLabel("���̸� ");
		JTextField rnTF = new JTextField();
		JButton chk = new JButton("����");
		public Room_Find() {
			setTitle("��ã��");
			setBounds(10,20,300,200);
			setLayout(null);
			roomNum.setBounds(30, 50, 70, 30);
			add(roomNum);
			rnTF.setBounds(100, 50, 120, 30);
			add(rnTF);
			chk.setBounds(110, 90, 70, 30);
			add(chk);
			chk.addActionListener(this);
			setVisible(true);
		}
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(e.getSource()==chk){
				sd.send_msg("FindRoom/"+rnTF.getText());
				dispose();
			}
		}

	}
	class Find_ID extends JFrame implements ActionListener {


		JLabel name = new JLabel("�̸�");
		JLabel email = new JLabel("e-mail");
		JTextField nametf = new JTextField();
		JTextField emailtf = new JTextField();
		JButton chk = new JButton("Ȯ��");

		public Find_ID() {
			setTitle("���̵�ã��");
			setBounds(20,20,300,250);
			setLayout(null);
			name.setBounds(50,50,50,30);
			add(name);
			email.setBounds(50,100,50,30);
			add(email);
			nametf.setBounds(100,50,150,30);
			add(nametf);
			emailtf.setBounds(100,100,150,30);
			add(emailtf);
			chk.setBounds(170, 150, 70, 30);
			add(chk);
			chk.addActionListener(this);
			setVisible(true);
		}
		public void actionPerformed(ActionEvent e) {

			sd.send_msg("Findid/"+nametf.getText()+"/"+emailtf.getText());
		}
	}
	class PW_QnA extends JFrame implements ActionListener{
		JLabel quiz = new JLabel("����");
		JLabel answer = new JLabel("��");
		JComboBox quiztf;
		JTextField answertf = new JTextField();
		JButton chk = new JButton("Ȯ��");

		public PW_QnA() {
			setTitle("����Ȯ�ο�QnA");
			setBounds(20,20,300,250);
			setLayout(null);
			quiz.setBounds(40,50,50,30);
			add(quiz);
			answer.setBounds(40,100,50,30);
			add(answer);
			Vector<String> quizArr = new Vector<>();
			quizArr.add("�� ������?");
			quizArr.add("�� ���� 1ȣ��?");
			quizArr.add("�� ��� �б���?");
			quizArr.add("�� ��� ������?");
			quiztf = new JComboBox(quizArr);
			quiztf.setBounds(90,50,150,30);
			add(quiztf);
			answertf.setBounds(90,100,150,30);
			add(answertf);
			chk.setBounds(170, 150, 70, 30);
			add(chk);
			chk.addActionListener(this);
			setVisible(true);
		}
		public void actionPerformed(ActionEvent e) {

			sd.send_msg("PWQnA/"+((String)quiztf.getSelectedItem())+"/"+answertf.getText());
		}
	}
	class PW_Change extends JFrame implements ActionListener {
		JLabel id = new JLabel("���̵�");
		JLabel pw = new JLabel("��й�ȣ");
		JLabel pwchk = new JLabel("�����ȣ Ȯ��");
		JTextField idtf = new JTextField();
		JPasswordField pwtf = new JPasswordField();
		JPasswordField pwchktf = new JPasswordField();
		JButton chk = new JButton("Ȯ��");
		public PW_Change() {
			setTitle("��й�ȣ ����");
			setBounds(20,20,300,250);
			setLayout(null);
			pw.setBounds(30,60,100,30);
			add(pw);
			pwchk.setBounds(30,100,100,30);
			add(pwchk);
			pwtf.setBounds(120,60,150,30);
			add(pwtf);
			pwchktf.setBounds(120,100,150,30);
			add(pwchktf);
			chk.setBounds(170, 150, 70, 30);
			add(chk);
			chk.addActionListener(this);
			setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {

			if(pwtf.getText().equals(pwchktf.getText()))
				sd.send_msg("PWchange/"+pwtf.getText()+"/"+pwchktf.getText());
			else new Pop_up("��й�ȣ ����ġ");


		}
	}
	class Find_PW extends JFrame implements ActionListener {

		JLabel name = new JLabel("ID");
		JLabel email = new JLabel("e-mail");
		JTextField nametf = new JTextField();
		JTextField emailtf = new JTextField();
		JButton chk = new JButton("Ȯ��");
		public Find_PW() {
			setTitle("��й�ȣã��");
			setBounds(20,20,300,250);
			setLayout(null);
			name.setBounds(50,50,50,30);
			add(name);
			email.setBounds(50,100,50,30);
			add(email);
			nametf.setBounds(100,50,150,30);
			add(nametf);
			emailtf.setBounds(100,100,150,30);
			add(emailtf);
			chk.setBounds(170, 150, 70, 30);
			add(chk);
			chk.addActionListener(this);
			setVisible(true);
		}
		public void actionPerformed(ActionEvent e) {
			sd.send_msg("FindPW/"+nametf.getText()+"/"+emailtf.getText());
		}
	}
	class Room extends JFrame {
		JPanel room = new JPanel();
		public JTextArea roomChat = new JTextArea();
		public JScrollPane rcJs = new JScrollPane(roomChat);
		JTextField chat = new JTextField();
		public Room() {
			room.setBounds(0, 0, 920, 690);
			room.setLayout(null);
			rcJs.setBounds(310, 100, 300, 300);
			roomChat.setEditable(false);
			room.add(rcJs);
			chat.setBounds(310, 410, 300, 30);
			room.add(chat);
		}

	}
	interface GameParameters{

		int width=250;
		int height=600;
		int numW = 6; //6�� �� ���� ���α���
		int inBlok = 12;  //�ִ� ���� �ִ� ���� ���α���
		int boom = 4; //4�� �̻��϶� �����
		int radius = 20;//NextView.setBounds(305,10,130,150);
		///////////////////////////////////////////////////
		int nextWH= 130;//�ؽ�Ʈ â ũ��.
		int nestHE= 150;

		Color colors[]={Color.RED,Color.BLUE,Color.GREEN, Color.YELLOW};
		//���� �����̴� ��

		int right=1,left=2,down=3, up=4;

		int right_or = 0, top_or=1,left_or=2, bottom_or=3;
		//B����  B��������     B�� ����      B������         B�� �Ʒ��� 

	}
	class Game_Room extends JFrame //��ü�г� �Դϴ�.
	{

		int color_index_blockC_2;
		int color_index_blockD_2;
		int color_index_blockC;// ���� �����ҋ� �̸����⸦ �����ֱ� ���ؼ�  int������ ���� ����.
		int color_index_blockD;//���� ����.
		Graphics gg = null;
		Container  c = getContentPane();

		PuyoPanel_1_1 myPanel_1; 	// �гβ�����°�
		NextPanel_1 nextPanel_1;	//�̸����� �г�.
		blockCD_1 CD_1=new blockCD_1();
		/////////////////////////////////////////////////
		String str;
		JLabel Point ;
		JLabel textp;
		JLabel time;
		Sender sd;
		/////////////////////////////////////////
		PuyoPanel_2 myPanel_2 ;			// ���� ����� ���� �������ؼ�.
		//	blockCD_2 CD_2=new blockCD_2();		// �̸� ���� �ҷ��� ��� �ϱ����ؼ� ���� ���Ӱ� ���� ����.
		/////////////////////////////////////////////////
		public Game_Room(Socket socket)//  ��ü �г��� ũ�� �� ����.
		{ 
			super(" �����̱⹫�� ");


			setBounds(20, 20, 920, 690);
			setLayout(null);
			makeGui();
			imageNext();
			makeGui_2();
			addWindowListener(new WinCC());
			sd = new Sender(socket);
			setVisible(true);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		}


		///////////////////////////////////////////////////////////////////////////////////////////
		void makeGui()// ��� Ŭ������  �������� �� ����ؼ�  �׸��� �ִ� �׸���  �� ����� �ִ� �޼ҵ�.
		{
			// �����̳ʷ�  ����� �ٿ� �־��� �����ӿ�.
			myPanel_1 = new PuyoPanel_1_1();
			myPanel_1.setBounds(5,20,255,600); // ���� ����� ũ��� ���Ǹ� ���� ���־���.

			c.add(myPanel_1, "Center");

		} 
		void imageNext()// �� �̸����� ��� ��ġ�� �������ش�,
		{
			//Container  c = getContentPane();
			nextPanel_1 = new NextPanel_1();
			nextPanel_1.setBounds(305,10,130,150);
			//	   add(nextPanel);
			c.add(nextPanel_1,"Center");//�̸� ���� �г��� ��������ϴ�.
		}
		////////////////////////////////////////////////
		void makeGui_2()//����� ��ΰ� ��ġ�� ���� �آZ��.
		{
			//Container  c = getContentPane();
			myPanel_2 = new PuyoPanel_2();// ���� ����� ����� ���Ӱ� ���� �� �آZ��.
			myPanel_2.setBounds(600,20,255,600); // ���� ����� ũ��� ���Ǹ� ���� ���־���.

			c.add(myPanel_2, "Center");// �����̳ʷ�  ����� �ٿ� �־��� �����ӿ�.


		}
		class WinCC extends WindowAdapter{

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				dispose();
				myPanel_1.showMessage();
				myPanel_1.animator1_2.stop();
				nextPanel_1.animator1_1.stop();
			}
		}
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		class Block{// ���� ���� ���ִ� Ŭ������ �������.,

			int  X,Y, lastX, lastY;//  x , y  �� �μ�..
			int radius;//  ������./
			Color color;// ���� ������ �ִ� Ŭ����.
			int pipe;    // �������� (��Ʈ��.)
			int pipe2;// ������ ��Ʈ��.
			boolean four;
			///////////////////////////////////////////////////////////////////////////////////////////     
			Block(int X, int Y, int radius, Color color)// ���� �����ڸ� ����� // ���� �׸����� ��ǥ��. ũ�⸦ �������ش�.
			{
				this.X= X;   
				this.Y= Y;
				lastX = X;
				lastY = Y;
				this.radius = radius;
				this.color  = color;     

				pipe =-1;// �������� �⺻�� -1 �Z��.
				pipe2=-1;//������2  �⺻�� -1 �� �Z��.
				four = false;//������ ������ ���ؼ� ���� ��.
			}
			String moveData()/// �ٲ�κ�.
			{// ��Ʈ�� ���׷� �����͸� �޽��ϴ�.
				// ���ϰ��� x,y  �ް� ���� ������ �´�.
				return X+","+Y+","+color.getRGB();
			}
			///////////////////////////////////////////////////////////////////////////////////////////
			public void paint(Graphics g){// ���� �׷��ش�.
				g.setColor(color);
				g.fillOval(X-radius,Y-radius, 2*radius,2*radius);//��ǥ��@ ũ�⸦ ���༭ ���� �׷��ش�.

			}
			///////////////////////////////////////////////////////////////////////////////////////////
			public boolean collideDown_1(Block b)// �浹 �������� �����Ͽ� �����·� ���� �ߴ�.
			{	// 
				// ������ ���� �ִ� ����  ���� ������ ��(�浹 �������� �����·�)�� ��ǥ���� ũ�ٸ�  �� �����·�  �����ش�.
				return Y+radius >= b.Y-radius;
				//���� �����ִ� ���� Y+������  ��      ���� �����̰��ִ� ���� Y+������ ���� ũ�ٸ� .
			}   
			//////////////////////////////////////////////////////////////////////////
			//�����Ǿ� �ִ� ����  ���� �����ϼ��ִ� ������ ������ ���� ���� ���ϰ� �ѱ� ���ؼ�.
			public boolean chkLeft(Block b)
			{// �l�� �ִ� ����  �����̴� ���� ��ǥ����  �������
				return b.X+radius <= Y-radius;
			}
			//////////////////////////////////////////////////////
			//���� �Ǿ� �ִ� ����  �����ϼ� ������ ������ �������� ���� ���ϰ� �ϱ����ؼ�.
			public boolean chkRight(Block b){
				return  b.chkLeft(this);
			}  
		}
		class blockPipe{// ���� �������� ����� �Z��.
			LinkedList blocks;// ��ũ�� ����Ʈ ������ ������ �����.
			public blockPipe(){
				blocks= new LinkedList();// ���Ӱ�  ��ũ�� ����Ʈ�� ���� ���ش�..
			}
			public void paint(Graphics g)//  �׷����� ����ؼ� ����Ʈ�� �Ѵ�.
			{
				Iterator itr= blocks.iterator();
				//���ͷ�����  ���� itr �� �����  = �׾ȿ� �� ������./���ͷ����� ������ �ִ´�.
				while(itr.hasNext())// �����͸� �ٲ��� ����Ѵ�.
				{
					Object element= itr.next();// ������Ʈ ��������   ��ȯ �����ش�.
					Block b = (Block) element;// (�� ����) �����͸�   �� b �� �ִ´�.
					b.paint(g);//  ����Ʈ �Ѵ�.
				}
			}
			public int getSize()// ��Ʈ�� �޼ҵ� ����� �����.
			{
				return blocks.size();// ���ϰ�  ���� . ũ�⸦ �����ش�./
			}
		}
		class BlockAB_1{  // ���� �׸��� �� A  B �� ������ ���ؼ� ������ Ŭ����.

			Block blockA; // �� Ŭ���� ������ A�� �����
			Block blockB;//  �� Ŭ���� ������ B�� �����.

			int blockB_OR;  //A�� �������� B�� ������ ��


			public void paint(Graphics g){// ���� ������༭ �׷��ִ� ��Ȱ�� ���ش�.
				blockA.paint(g);
				blockB.paint(g);

			}
		}
		///////////////////////////////////////////////////////////////\
		class blockCD_1////???
		{
			Block blockC;
			Block blockD;
			public void paint(Graphics g){// ���� ������༭ �׷��ִ� ��Ȱ�� ���ش�.
				blockC.paint(g);
				blockD.paint(g);
			}
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		public class PuyoGame1_1  implements GameParameters 
		{
			public void startGame1_1_1()
			{
				CD_1=new blockCD_1();
				nextBB_1();
			}
			public void nextBB_1()//������ ���� ���� ���� ���ش�.
			{
				Random r = new Random();
				int colorindex = r.nextInt(4);//  �÷��� ����� �������� 4���� ���� �ְ� �������.
				int width=125;
				//NextView.setBounds(305,10,130,150);
				Block blockC= new Block(width/2-25, 20, 20, colors[colorindex]);// ���� ����.
				color_index_blockC=colorindex;
				colorindex= r.nextInt(4);// ���� �ٸ��� �ؾ��ؼ� �ٽ� �÷��� �Z��..
				Block blockD= new Block(width/2+15, 20, 20,  colors[colorindex]);
				color_index_blockD=colorindex;
				CD_1.blockC= blockC;//A���̶�� ���� ����.
				CD_1.blockD =blockD;//B ���̶�� ���� ����.
			}
			public void Render1(Graphics g)// �� �׸�? ��Ȳ��?  ��� �׷��ִ� ��Ȱ../
			{ 
				CD_1.paint(g);
			}
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		public class PuyoGame1   implements GameParameters {//���� ���̽� ���� Ǫ�� ����..

			PuyoGame1_1 pg2;
			BlockAB_1  AB_1; // ����ڰ� ���� �����ϼ� �ִ� ¦ ��Ʈ//  ���� A   B  �� ������ �׷��ִ� ��Ȱ�� �Ѵ�.
			blockPipe [] pipes_1;//  �������� �迭�� ����. ������ �Ҵ� �Ѵ�.
			LinkedList mBlocks_1;// ��ũ�� ����Ʈ ���� �����͸� ���� ������ ������ش�.
			int score =0;//  
			public void StartGame_1()//  ������ ��ŸƮ  �޼ҵ�
			{
				AB_1 = new BlockAB_1();//���� ������ִ� Ŭ����  ���Ӱ� ���� ���ش�.
				mBlocks_1 = new LinkedList();// ��ũ�� ����Ʈ�� ���Ӱ� ���� �ߴ�.  �������� �ʴ� ��.! ����� ���� ��.
				pipes_1 = new blockPipe[numW];// ������ �迭��  �ִ� ��� �������� �������� ���Ӱ� �����آZ��.
				// Ŭ������ ���Ӱ� ���Ǹ� ����.
				for (int i = 0; i < numW ; i++) //  6  ���� ���鼭.
					pipes_1[i] =new blockPipe();// ������ ������. 6���� �������� ����� �ְ�.(��)
				firstAB_1();// ù��° �żҵ带 ���� ���̴�.
			}
			public void  firstAB_1()// ���� ũ�� �� ��ǥ�� ���� ���ִ� �޼ҵ�.
			{
				Random r = new Random();// �������� �����.
				int left =2;// ��Ʈ�� �� 2��.
				int right = left+1;// ����Ʈ +1    =  ����Ʈ..
				int colorindex = r.nextInt(4);//  �÷��� ����� �������� 4���� ���� �ְ� �������.
				Block blockA_1= new Block(width/2-25,20, 20, colors[color_index_blockC]);// ���� ����.
				colorindex= r.nextInt(4);// ���� �ٸ��� �ؾ��ؼ� �ٽ� �÷��� �Z��..
				Block blockB_1= new Block(width/2+15, 20, 20, colors[color_index_blockD]);
				blockA_1.pipe =left;// A���� �������� =2
				blockB_1.pipe = right;// B�� �������� 3 ���� ����.
				//AB ����
				AB_1.blockA= blockA_1;//A���̶�� ���� ����.
				AB_1.blockB =blockB_1;//B ���̶�� ���� ����.
				AB_1.blockB_OR= right_or;//��ó�� ������ ������  B���� �׻� A���� ���������� �ϱ����ؼ�.
				new PuyoGame1_1().nextBB_1();
			}
			public boolean Update_1()//�� ���� �޼ҵ�./
			{
				moveMBlocks_1();// ����MB���� ���¤���
				movepair_1();
				boolean gameOver = false; // ������ ��������..  �׳� �������� �ذ�.
				for (int i = 0; (i<numW) && !gameOver ; i++) // 
					gameOver = (pipes_1[i].getSize() > inBlok);
				return gameOver;
			}
			////////////////////////////////////////////////////////////////////////
			public int getScore_1(){
				return score;
			}
			////////////////////////////////////////////////////////////////////////
			public void render_1(Graphics g)// �� �׸�? ��Ȳ��?  ��� �׷��ִ� ��Ȱ../�����带 �����ϴºκ�.
			{
				AB_1.paint(g);//�� �׷��ش�  A,B ��//�����̴� ���̵��� ���� �׷��ش�.
				for (int i = 0; i < numW; i++) //�����׷���.
					pipes_1[i].paint(g);
				for (int i = 0; i < mBlocks_1.size(); i++) // �����ִ� �� �� �׷���.
				{
					Block bk = (Block) mBlocks_1.get(i);
					bk.paint(g);// ����Ʈ���شٰ���
				}
			}
			////////////////////////////////////////////////////////////////////////
			boolean moveBlock_1(Block bk)//���� ���ư��� ���� ������ �´�.
			{
				if (! pipes_1[bk.pipe].blocks.isEmpty())//���� ������S [����� ������].
				{
					if(!bk.collideDown_1((Block) pipes_1[bk.pipe].blocks.getLast()))
					{// ���� ���� �����̴� ���� 
						bk.Y+=1;
						//               System.out.println(bk.pipe +" pipe --1");
						//					if(bk.moving())
						//						System.out.println("�پҴ�");
						return true;

					}
					else
					{   
						bk.pipe2 = pipes_1[bk.pipe].blocks.size();
						pipes_1[bk.pipe].blocks.add(bk);
						//   System.out.println(bk.pipe +" pipe--2");
						return false;
					}
				}
				if(!bound_1(bk, down)){
					bk.Y+=1;
					return true;
				}
				else 
				{
					bk.pipe2 = pipes_1[bk.pipe].blocks.size();
					pipes_1[bk.pipe].blocks.addLast(bk);
					return false;
				}

			}
			void movepair_1(){//  A,B�� �����϶� ��Ȳ�� �����ش�.//
				boolean ba = moveBlock_1(AB_1.blockA);
				boolean bb = moveBlock_1(AB_1.blockB);
				if((!ba) && (AB_1.blockB_OR==top_or))
				{
					chkColor_1(AB_1.blockA);
					if(AB_1.blockA.color!=AB_1.blockB.color)
						chkColor_1(AB_1.blockB);
					firstAB_1();//�� ���� �ϴ� �޼ҵ�/

				}

				//B���� �������� �ʰ�  A�� �Ʒ�������
				else if ((!bb) && (AB_1.blockB_OR==bottom_or))
				{
					pipes_1[AB_1.blockB.pipe].blocks.add(AB_1.blockA);
					AB_1.blockA.pipe2= AB_1.blockB.pipe2+1;
					chkColor_1(AB_1.blockB);
					if(AB_1.blockA.color!=AB_1.blockB.color)
						chkColor_1(AB_1.blockA);
					firstAB_1();

				}
				//a�� �����̰� b�� �������� ������
				else if (ba && (!bb) )
				{  
					// a���� mBlock�� �־��
					mBlocks_1.addLast(AB_1.blockA);
					chkColor_1(AB_1.blockB);
					firstAB_1();
				}

				else if( bb && (!ba) )
				{ 
					mBlocks_1.addLast(AB_1.blockB);
					chkColor_1(AB_1.blockA);
					firstAB_1();

				}
				//�Ѵ� �����϶� �������� ������
				else if (!(ba && bb))
				{ 
					if(AB_1.blockA.color!=AB_1.blockB.color)
						chkColor_1(AB_1.blockA);
					chkColor_1(AB_1.blockB);
					firstAB_1();
				}
			}
			//��Ʈ�� �Ҽ����� �����̴� ���̵�
			////////////////////////////////////////////////////////////////////////      
			void moveMBlocks_1 ()// �����ִ� ����  �ο��ִ� ���̵�.
			{
				//LinkedList mBlocks
				ListIterator itr =  mBlocks_1.listIterator();
				//��ũ�� ���ͷ����� ���� itr = ��ũ�帮��Ʈ������ �����͸� ��ũ�����ͷ����� �������� �ٲ��ְ�.
				while(itr.hasNext())// ��ȯ ��Ų �ڷḦ ������ while �������� ������ �ͼ� ������.
				{
					Block bk = (Block) itr.next();// ������� ������ ���������� �ٲ۴�.
					//            System.out.println(AB.blockA.color+"    ����");
					if(!moveBlock_1(bk))//�����̴� ��(bk )�� �ƴ϶�� 
					{
						ListIterator litr = itr;

						itr.previous();
						chkColor_1((Block)itr.next());
						itr.previous();
						litr.remove();

					}
				}
			}
			////////////////////////////////////////////////////////////////////////     
			public void ProcessKey(KeyEvent e){

				int key = e.getKeyCode();
				switch(key){
				case KeyEvent.VK_LEFT:
					Control(left);
					break;
				case KeyEvent.VK_RIGHT:
					Control(right);
					break;
				case KeyEvent.VK_DOWN:
					Control(down);
					break;
				case KeyEvent.VK_UP:
					Rotate();
					break;
				case KeyEvent.VK_SPACE:
					space();///////////
					break;
				}
			}
			////////////////////////////////////////////////////////////////////////
			boolean bound_1(Block bk,int dir)  //  ����(����) �� �Ҹ���.. ������ ã�ƶ�.
			{
				switch(dir)
				{
				case left :
					return bk.pipe ==0;
				case right : 
					return bk.pipe ==5;
				case down :
				{
					return  bk.Y+20 > height;
				}

				}
				return false;
			}
			////////////////////////////////////////////////////////////////////////
			boolean moveRight(Block bk){
				if(	!pipes_1[bk.pipe+1].blocks.isEmpty())
					if(	bk.collideDown_1((Block)pipes_1[bk.pipe+1].blocks.getLast()))
						if(bk.chkLeft((Block)pipes_1[bk.pipe+1].blocks.getLast()))	  
							return false;
				bk.pipe++;
				bk.X+=2*radius;

				return true;
			}
			boolean moveLeft(Block bk){
				if(	!pipes_1[bk.pipe-1].blocks.isEmpty())
					if(	bk.collideDown_1((Block)pipes_1[bk.pipe-1].blocks.getLast()))
						if(bk.chkLeft((Block)pipes_1[bk.pipe-1].blocks.getLast()))	  
							return false;
				bk.pipe--;
				bk.X-=2*radius;
				return true;
			}
			boolean MoveBlock2(Block bk){//���⼭ ���ͽ���

				if (! pipes_1[bk.pipe].blocks.isEmpty()) //�ؿ� ���� ������ 
				{
					if(!bk.collideDown_1((Block) pipes_1[bk.pipe].blocks.getLast()))
					{
						bk.Y+=1;
						return true;
					}
					else
					{	
						bk.pipe2 = pipes_1[bk.pipe].blocks.size(); 
						pipes_1[bk.pipe].blocks.add(bk);
						return false;
					}
				}
				if(!bound_1(bk, down)){
					bk.Y+=1;
					return true;
				}
				else 
				{
					bk.pipe2 = pipes_1[bk.pipe].blocks.size();
					pipes_1[bk.pipe].blocks.addLast(bk);
					return false;
				}
			}
			public void space()
			{
				boolean ba = MoveBlock2(AB_1.blockA);
				boolean bb = MoveBlock2(AB_1.blockB);
				int limit =  height-20;
				if((!ba) && (AB_1.blockB_OR==top_or))
				{
					chkColor_1(AB_1.blockA);

					if(AB_1.blockA.color != AB_1.blockB.color)
						chkColor_1(AB_1.blockB);
					System.out.println(AB_1.blockA.X+"   2��x ��ǥ");
					System.out.println(AB_1.blockB.Y+"    2�� y��ǥ");

					firstAB_1();
				}
				else if ((!bb) && (AB_1.blockB_OR==bottom_or))
				{
					pipes_1[AB_1.blockB.pipe].blocks.add(AB_1.blockA);
					AB_1.blockA.pipe2= AB_1.blockB.pipe2+1;

					chkColor_1(AB_1.blockB);

					if(AB_1.blockA.color != AB_1.blockB.color)
						chkColor_1(AB_1.blockA);

					System.out.println(AB_1.blockA.X+"   2��x ��ǥ");
					System.out.println(AB_1.blockB.Y+"    2�� y��ǥ");
					firstAB_1();

				}
				//a�� �����̰� b�� �������� ������
				else if (ba && (!bb) )
				{
					// a���� mBlock�� �־��
					mBlocks_1.addLast(AB_1.blockA);

					chkColor_1(AB_1.blockB);
					System.out.println(AB_1.blockA.X+"   2��x ��ǥ");
					System.out.println(AB_1.blockB.Y+"    2�� y��ǥ");
					firstAB_1();
				}

				else if( bb && (!ba) )
				{
					mBlocks_1.addLast(AB_1.blockB);
					chkColor_1(AB_1.blockA);
					firstAB_1();

				}
				//�Ѵ� �����϶� �������� ������
				else if (!(ba && bb))
				{
					if(AB_1.blockA.color!=AB_1.blockB.color)
						chkColor_1(AB_1.blockA);
					chkColor_1(AB_1.blockB);
					System.out.println(AB_1.blockA.X+"   2��x ��ǥ");
					System.out.println(AB_1.blockB.Y+"    2�� y��ǥ");
					firstAB_1();
				}

				if(!bound_1(AB_1.blockA, down))
				{
					AB_1.blockA.Y= limit- pipes_1[AB_1.blockA.pipe].getSize()*40 ;
					AB_1.blockB.Y= limit - pipes_1[AB_1.blockB.pipe].getSize()*40 ; 
				}
				if(!bound_1(AB_1.blockB, down))
				{
					AB_1.blockA.Y= limit- pipes_1[AB_1.blockA.pipe].getSize()*40 ;
					AB_1.blockB.Y= limit - pipes_1[AB_1.blockB.pipe].getSize()*40 ; 
				}
				int i = AB_1.blockB_OR;
				switch(i)
				{
				case right_or:
				{
					AB_1.blockA.Y= limit - pipes_1[AB_1.blockA.pipe].getSize()*40 ;
					AB_1.blockB.Y= limit - pipes_1[AB_1.blockB.pipe].getSize()*40 ; 
					break;
				}
				case top_or:
				{
					if(AB_1.blockA.pipe>=0 && AB_1.blockB.pipe >= 0)
					{
						AB_1.blockA.Y= limit - pipes_1[AB_1.blockA.pipe].getSize()*40 ;
						AB_1.blockB.Y= limit - (pipes_1[AB_1.blockB.pipe].getSize()+1)*40 ; 

					}
					break;
				}
				case left_or:
				{
					AB_1.blockA.Y= limit - pipes_1[AB_1.blockA.pipe].getSize()*40 ;
					AB_1.blockB.Y= limit - pipes_1[AB_1.blockB.pipe].getSize()*40 ; 
					break;
				}
				case bottom_or:
				{
					if(AB_1.blockA.pipe>=0 && AB_1.blockB.pipe >= 0){

						AB_1.blockA.Y= limit - (pipes_1[AB_1.blockA.pipe].getSize()+1)*40 ;
						AB_1.blockB.Y= limit - pipes_1[AB_1.blockB.pipe].getSize()+1*40 ; 
					}
					break;
				}
				}   
			}
			public void Control(int dir){//����(����)
				switch(dir)
				{
				case  down :
				{
					switch(AB_1.blockB_OR){
					case top_or:
						if(!bound_1(AB_1.blockA,down))
						{
							AB_1.blockA.Y+=20;
							AB_1.blockB.Y+=20;
						}
						break;
					default:
						if(!bound_1(AB_1.blockB,down))
						{
							AB_1.blockA.Y+=20;
							AB_1.blockB.Y+=20;
						}
						break;
					}
					break;
				}
				////////////////////////////////////////////////////////////////
				case left :
				{

					switch(AB_1.blockB_OR)
					{
					case right_or:
					{
						if(!bound_1(AB_1.blockA,left))
							if(moveLeft(AB_1.blockA))
								moveLeft(AB_1.blockB);
						break;
					}
					case top_or :
					{
						if(!bound_1(AB_1.blockA, left))
							if(moveLeft(AB_1.blockA))
								moveLeft(AB_1.blockB);
						break;
					}
					case left_or :
					{
						if(!bound_1(AB_1.blockB, left))
							if(moveLeft(AB_1.blockB))
								moveLeft(AB_1.blockA);
						break;
					}
					case bottom_or:
					{
						if(!bound_1(AB_1.blockA, left))
							if(moveLeft(AB_1.blockB))
								moveLeft(AB_1.blockA);
						break;
					}
					}
					break;

				}
				case right :
				{
					switch(AB_1.blockB_OR)
					{
					case right_or:
					{
						if(!bound_1(AB_1.blockB, right))
							if(moveRight(AB_1.blockB))
								moveRight(AB_1.blockA);
						break;
					}
					case top_or:
					{
						if(!bound_1(AB_1.blockB, right))
							if(moveRight(AB_1.blockA))
								moveRight(AB_1.blockB);
						break;
					}
					case left_or:
					{
						if(!bound_1(AB_1.blockA, right))
							if(moveRight(AB_1.blockA))
								moveRight(AB_1.blockB);
						break;

					}

					case bottom_or:
					{
						if(!bound_1(AB_1.blockB,right ))
							if(moveRight(AB_1.blockB))
								moveRight(AB_1.blockA);
						break;
					}

					}
					break;
				}
				}
			}
			public void Rotate(){//ȸ��
				int i = AB_1.blockB_OR;
				switch(i)
				{
				case right_or:
				{
					AB_1.blockB_OR=top_or;
					AB_1.blockB.X=AB_1.blockA.X;
					AB_1.blockB.Y=AB_1.blockA.Y -40;
					AB_1.blockB.pipe = AB_1.blockA.pipe;

					break;

				}
				case top_or:
				{
					if(AB_1.blockB.pipe>0)
					{
						boolean downchk = true;//�ʱⰪ ����.
						if(!pipes_1[AB_1.blockA.pipe-1].blocks.isEmpty())
						{//ȸ���� ���� �� ������ ���� äũ�ؼ� ���� ���ϰ��Ұ��̴�.
							if(AB_1.blockA.collideDown_1((Block)pipes_1[AB_1.blockA.pipe-1].blocks.getLast()))
							{
								if(AB_1.blockA.chkLeft((Block)pipes_1[AB_1.blockA.pipe-1].blocks.getLast())){
									downchk=false;
								}
							}
						}
						if(downchk){// ����� ������ ȸ���� �����ϴ�.
							AB_1.blockB.Y =AB_1.blockA.Y;
							AB_1.blockB.X= AB_1.blockA.X -40;
							AB_1.blockB_OR=left_or;
							AB_1.blockB.pipe--;
						}
					}
					break;
				}
				case left_or:
				{	 //���� �츮���� ȸ������ ���� �ε�ĥ ���̴�.
					if(bound_1(AB_1.blockB, down))
						break;
					AB_1.blockB_OR =bottom_or;
					AB_1.blockB.Y = AB_1.blockA.Y +40;
					AB_1.blockB.X = AB_1.blockA.X;
					AB_1.blockB.pipe = AB_1.blockA.pipe;
					break;

				}
				case bottom_or:// �����̴� ��ü�� �ؿ� �ְ�  up�� �������� ������ ���� �ö󰡴� ��Ȳ.
				{//��ü�� �����ʿ� ������  �������� ���ϰ� �����ֱ����ؼ�
					if(AB_1.blockB.pipe < 5)
					{
						boolean downchk = true;//�ʱⰪ ����.
						if(!pipes_1[AB_1.blockB.pipe+1].blocks.isEmpty())
						{//ȸ���� ���� �� ������ ���� äũ�ؼ� ���� ���ϰ��Ұ��̴�.
							if(AB_1.blockB.collideDown_1((Block)pipes_1[AB_1.blockB.pipe+1].blocks.getLast()))
							{
								if(AB_1.blockB.chkLeft((Block)pipes_1[AB_1.blockB.pipe+1].blocks.getLast())){
									downchk=false;
								}
							}
						}
						if(downchk)//true �� �׳� ����,
						{
							AB_1.blockB_OR =right_or;
							AB_1.blockB.Y= AB_1.blockA.Y;
							AB_1.blockB.X =AB_1.blockA.X +40;
							AB_1.blockB.pipe++;
						}
					}

					break;
				}
				}
			}
			/////////////////////////////////////////////////////////////////////
			//����� ������ ���� �ִ� ��Ȯ�� �� ��Ʈ���� ���ؼ�  ������
			void chkColor_1(Block bk)//��������. 1��.
			{
				LinkedList finshbk = new LinkedList<>();
				LinkedList list = new LinkedList<>();

				finshbk.add(bk);//���� �� ����Ʈ�� ����������, ������ ��Ҹ� �߰��մϴ�
				bk.four= true;//  �����·� ��������� Ȯ���Ϸ���. ���·� �޴´�

				Iterator itr = finshbk.iterator();//���ͷ����� ���� �������� ���� ������ �ð��̴�

				while(itr.hasNext())// ���� ���� �����鼭  Ȯ���� �Ұ��̴�.
				{
					Object itrOB = itr.next(); //������Ʈ �������� ��ȯ �ؼ��������̴�.
					Block block =(Block) itrOB;// ���������� �ٽ� �ٲ۴�. 
					LinkedList friend= friends_1(block);//�ؿ���  �������� ���� ��ũ�� ����Ʈ�� �޴´�.

					for (int i = 0; i < friend.size(); i++) // �װ�����ŭ  FOR���� ������.
					{
						Block friend_1= (Block)friend.get(i);
						//�� ������ ���޴´� =     ���� �������� �޾Ƽ� �ٲ��ش�.
						if((!friend_1.four)&&(friend_1.color==block.color))
						{// ����. �������ǰ���  ������ �ƴϰ�  &&  ���� ���ٸ�.
							finshbk.add(friend_1);// �׺귰�� �������� �־��ش�.
							friend_1.four=true;// �װ��� ���� �̴�.
						}
					}
					list.add(finshbk.remove());//�� ����Ʈ�κ��� ������ ��Ҹ� ������ �����ݴϴ�.
					itr=finshbk.iterator();
					//	iterator ()
					//�� ����Ʈ���� ��Ҹ�(������ ������) �ݺ� ó���ϴ� ���׷���Ÿ�� �����ݴϴ�.
				}
				/////////////////////////////////////
				if(list.size() >= boom)// ������ ���� ����   4���� ũ�ٸ�. 
					for (int i = 0; i < list.size(); i++) 
					{
						Object OBJ =list.get(i);//�װͿ� �����͸� ������Ʈ�� �ٲٰ�.
						Block block =(Block)OBJ;// �����·� ����ȯ.
						eraseBB_1(block);// �׺����� ����� �޼ҵ�� ����.
					}
				else
					for (int i = 0; i < list.size(); i++) 
					{
						Object OB = list.get(i);
						Block block=(Block)OB;
						block.four=false;
					}

			}
			//2��
			LinkedList friends_1(Block block)//ģ�� �ֺ��� ���� �ִ��� Ȯ��.
			{//��ũ�� ����Ʈ�������� ���� �ٽ� ���� �Ұ��̴�.
				LinkedList friend = new LinkedList();
				//    	  pipe =-1;// �������� �⺻�� -1 �Z��.
				//          pipe2=-1;/
				int frpipe = block.pipe;
				int frpipe2 = block.pipe2;

				if(block.pipe>0)//2_1��//�� ���� ������ ���̰� 0���� Ŭ��.
					if(pipes_1[frpipe-1].blocks.size()>frpipe2)
						//������ �迭[������ ����-1]�� ������ ����� >  ������ ������ ������ ���̺��� Ŭ��.
						friend.add(pipes_1[frpipe-1].blocks.get(frpipe2));
				//        �������̴�  .  ->  ������ �迭��[����������������-1]�鸦.(���� ���� ���α��̷� )�� ����Ʈ���� ������ ��ġ�� �ִ� ��Ҹ� �����ݴϴ�.

				///////////////////////////////////////////////////////
				if(block.pipe<numW-1 )//2_1��// ���� �ķ��� ���̰�.  < 6-1 (5)
					if(pipes_1[frpipe+1].blocks.size() > frpipe2)
						//���� ����������[���������α���+1].������ �����  >  ������ ���� ������ ���̺���
						friend.add(pipes_1[frpipe+1].blocks.get(frpipe2));
				////////////////////////////////////////////////////
				if(pipes_1[frpipe].blocks.size()>frpipe2+1)//2_3��.
					//�������迭[������������ ���� ������]������ ����� > ����+1 ���� ������.
					friend.add(pipes_1[frpipe].blocks.get(frpipe2+1));
				//////////////////////////////////////////////
				if(frpipe2-1>-1)//�� ����Ʈ�� ����������, ������ ��Ҹ� �߰��մϴ�.  addLast
					friend.add(pipes_1[frpipe].blocks.get(frpipe2-1));

				return friend;
			}
			//3��
			void eraseBB_1(Block block)
			{
				for (int i = block.pipe2+1; i <pipes_1[block.pipe].blocks.size(); i++) 
				{
					Object OB = pipes_1[block.pipe].blocks.get(i);
					Block block_2 = (Block) OB;
					block_2.pipe2--;
					block_2.Y+= 2*radius;
				}
				pipes_1[block.pipe].blocks.remove(block.pipe2);
				score++;
			}
		}
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		class NextPanel_1 extends JPanel implements Runnable,GameParameters
		{
			int PWIDTH1 = nextWH;
			int PHEIGHT1 = nextWH;
			Thread animator1_1;// �����带 ������ ���ؼ�.
			boolean running1_1 = false;   // �����·� �Z��
			Game_Room topFrame1_1;// ��ü �гο��� ������ ��� �ϱ� ���ؼ�.
			PuyoGame1_1 nextPuyo1_1;//  �ҷ��� ���. �ϰٴ�/
			Graphics dbg1_1; 
			Image dbImage1_1 = null;
			public NextPanel_1() 
			{
				setBackground(Color.white);

				setPreferredSize( new Dimension(PWIDTH1, PHEIGHT1));// ��� ������ �����ش�.
				//setBounds(20, 30, 150, 150);
				//	       setFocusable(true);// �ּ� Ǯ�� �ȿ����δ�/

				nextPuyo1_1= new PuyoGame1_1();// ���Ӱ� ���� ���ְ�.     ������    ������
				nextPuyo1_1.startGame1_1_1();// myPuyo �ȿ� �մ� �� ������ �������� ��ŸƮ �ϰ�
				startGame1_1();// �����带 �����Ѵ�.
			}
			void startGame1_1()
			{ 
				if (animator1_1 == null || !running1_1) {// �ȵ��� ������... ���Ѵ�?
					animator1_1 = new Thread(this);
					animator1_1.start();//�����带 �����Ѵ�. run  �޼ҵ� ����.
				}
			} 
			public void run()//������ ���� �κ� �����尡 ���۰�����
			{

				running1_1 = true;

				while(running1_1) {
					gameRender1_1();   // ���ۿ� ����.  ������ �׷��ִ¿�
					paintScreen1_1();  // ȭ�� ���۸� �׸�
				}
			}
			void gameRender1_1()//  ���  �׸�  ����ִ°�  ��  ���̸� �������༭ �׸� �׷���.
			{
				if (dbImage1_1 == null){
					dbImage1_1 = createImage(PWIDTH1, PHEIGHT1);// ���� ���� �׸�// �׸��� ������ ���.

					return;

				}

				else{
					dbg1_1 = dbImage1_1.getGraphics(); 
				}

				dbg1_1.setColor(Color.black);
				dbg1_1.fillRect (0,0, PWIDTH1, PHEIGHT1);//��ü ��ʾ��� ��ġ(���̴� ���)

				nextPuyo1_1.Render1(dbg1_1);// �׸� �׸�?
			}  
			void paintScreen1_1() 
			{ 
				Graphics g;
				try {
					g = this.getGraphics();
					if ((g != null) && (dbImage1_1 != null))
						g.drawImage(dbImage1_1, 0, 0, null);
					g.dispose();
				}
				catch (Exception e)
				{ 

				}
			} 
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		class PuyoPanel_1_1 extends JPanel implements Runnable, GameParameters
		{	// �г��� �ް�   �гο�  ���ʺ� @  �׷��� ���..
			////////////////////////////////////////////////////////////////////////
			int PWIDTH_1 = width; // ���� 
			int PHEIGHT_1 = height; //����
			Thread animator1_2;// �����带 ������ ���ؼ�.
			int stime=0;
			boolean running = false;   // �����·� �Z��
			boolean gameOver = false;//////////////////
			Game_Room topFrame1_2;// ��ü �гο��� ������ ��� �ϱ� ���ؼ�.
			PuyoGame1 myPuyo1_2;//  �ҷ��� ���. �ϰٴ�/
			int score = 0;
			Graphics dbg1_2; 
			Image dbImage1_2 = null;
			StringTokenizer st;

			///////////////////////////////////////////////////////////////////////
			public PuyoPanel_1_1()
			{
				setBackground(Color.red);// �ѿ� ��λ�

				setPreferredSize( new Dimension(PWIDTH_1, PHEIGHT_1));// ��� ������ �����ش�.
				//setBounds(20, 30, 150, 150);
				setFocusable(true);


				myPuyo1_2= new PuyoGame1();// ���Ӱ� ���� ���ְ�.
				myPuyo1_2.StartGame_1();// myPuyo �ȿ� �մ� �� ������ �������� ��ŸƮ �ϰ�//
				startGame();// �����带 �����Ѵ�.

				addKeyListener( new KeyAdapter() {

					public void keyPressed(KeyEvent e)
					{ 
						myKeyPressed(e); }
				});
			}  
			////////////////////////////////////////////////////////////////////////
			void myKeyPressed(KeyEvent e)
			{ 
				myPuyo1_2.ProcessKey(e);
			}
			////////////////////////////////////////////////////////////////////////
			void startGame()
			{ 
				if (animator1_2 == null || !running) {// �ȵ��� ������... ���Ѵ�?
					animator1_2 = new Thread(this);
					animator1_2.start();//�����带 �����Ѵ�. run  �޼ҵ� ����.
				}
			}
			////////////////////////////////////////////////////////////////////////
			public void run()//������ ���� �κ� �����尡 ���۰�����
			{
				running = true;
				int maxtime=8000;///////////
				int cnt =0;
				while(running) {
					gameUpdate(); //�� ������ ����? �޼ҵ�.
					gameRender();   // ���ۿ� ����.  ������ �׷��ִ¿�
					paintScreen();  // ȭ�� ���۸� �׸�
					cnt++;///// �����带 �����鼭 
					if(cnt%100==0)
					{
						String str ="";
						for (blockPipe bp : myPuyo1_2.pipes_1) {

							for (Object oo :  bp.blocks) {
								Block bb = (Block)oo;
								str +=bb.moveData()+"&";
							}
						}
						if(!str.equals(""))
							sd.send_msg("Coord/"+myrom+"/"+myid+"/"+str);
					}// �ٲ�κ�.

					if(myPuyo1_2.Update_1()){
						sd.send_msg("Gameover/"+myrom+"/"+score);
						new Pop_up("��� ��ٸ�����!");
						break;
					}

					if(stime == maxtime){
						sd.send_msg("Gameover/"+myrom+"/"+score);
						new Pop_up("��� ��ٸ�����!");
						break;
					}
					try {
						Thread.sleep(20);
						time.setText("TIME : "+ stime/100+"sec");
					} catch (Exception e) {
					}
				}
			}
			//////////////////////////////////////////////////////////
			void showMessage() {
				JOptionPane.showMessageDialog(null,"  GAME OVER   "+"\n"+" ȹ�� ���� "+ myPuyo1_2.score+ "��"+ "   �� �� "+stime/100 +"sec",
						"M",JOptionPane.WARNING_MESSAGE);
			}
			////////////////////////////////////////////////////////////////////////
			void gameUpdate() 
			{ 
				myPuyo1_2.Update_1();// �̰� �ּ� �ϸ� �����̴� ��Ȱ.
			}  
			///////////////////////////////////////////////////////////////////////
			void gameRender()//  ���  �׸�  ����ִ°�  ��  ���̸� �������༭ �׸� �׷���.
			{
				if (dbImage1_2 == null){
					dbImage1_2 = createImage(PWIDTH_1, PHEIGHT_1);// ���� ���� �׸�// �׸��� ������ ���.
					return;
				}
				else{
					dbg1_2 = dbImage1_2.getGraphics(); 
				}
				//���
				dbg1_2.setColor(Color.darkGray);
				dbg1_2.fillRect (0,0, PWIDTH_1, PHEIGHT_1);//��ü ��ʾ��� ��ġ(���̴� ���)
				myPuyo1_2.render_1(dbg1_2);// �׸� �׸�?
			}  
			//    gameRender()     paintScreen()  �ΰ��� ��Ʈ ���Ѵ� ������,,,����,  
			void paintScreen()  // ��Ȳ ����  ��ũ���� �׷��ִ� ��Ȱ�� �ϴ¸޼ҵ�.
			{ 
				Graphics g;
				try {
					g = this.getGraphics();
					if ((g != null) && (dbImage1_2 != null))
						g.drawImage(dbImage1_2, 0, 0, null);
					g.dispose();
				}
				catch (Exception e)
				{ 
				}
			} 

		}
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		class PuyoPanel_2 extends JPanel implements GameParameters
		{	// �г��� �ް�   �гο�  ���ʺ� @  �׷��� ���..
			////////////////////////////////////////////////////////////////////////
			int PWIDTH = width; // ���� 
			int PHEIGHT = height; //����
			Image img = null;
			////////////////////////////////////////////////////////////////////////
			public void paint(Graphics g)
			{
				if(img==null)
				{
					img = createImage(500,600);
					gg = img.getGraphics();
					gg.setColor(Color.white);
					gg.fillRect(0, 0, 500, 600);
				}
				g.drawImage(img, 0, 0, this);

			}
			void test(String aa, String bb, String cc)
			{

				int a=Integer.parseInt(aa);
				int b=Integer.parseInt(bb);
				int c=Integer.parseInt(cc);
				gg.setColor(new Color(c));
				gg.fillOval(a, b, 40, 40);
				repaint();
			}
		}

	}

	public static void main(String[] args) {
		new MainFrame();
	}
}
