package myftp;




import java.awt.Button;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;



import javax.print.attribute.standard.JobOriginatingUserName;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JOptionPane;
public class FtpUtils {
        //ftp��������ַ
        public static String hostname = "127.0.0.1";
        //ftp�������˿ں�Ĭ��Ϊ21
        public static Integer port = 21 ;
        //ftp��¼�˺�
        public static String username = "admin";
        //ftp��¼����
        public static String password = "123";
        public static String nowpath = "";
        
        public FTPClient ftpClient = null;
        
        /**
         * ��ʼ��ftp������
         * @return 
         */
        public boolean initFtpClient() {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            try {
                System.out.println("connecting...ftp������:"+this.hostname+":"+this.port); 
                ftpClient.connect(hostname, port); //����ftp������
                ftpClient.login(username, password); //��¼ftp������
                int replyCode = ftpClient.getReplyCode(); //�Ƿ�ɹ���¼������
                if(!FTPReply.isPositiveCompletion(replyCode)){
                    System.out.println("connect failed...ftp������:"+this.hostname+":"+this.port);
                    return false;
                }
                System.out.println("connect successfu...ftp������:"+this.hostname+":"+this.port); 
                return true;
            }catch (MalformedURLException e) { 
               e.printStackTrace(); 
            }catch (IOException e) { 
               e.printStackTrace(); 
            } 
            return false;
        }

        /**
        * �ϴ��ļ�
        * @param pathname ftp���񱣴��ַ
        * @param fileName �ϴ���ftp���ļ���
        *  @param originfilename ���ϴ��ļ������ƣ����Ե�ַ�� * 
        * @return
        */
        public boolean uploadFile( String pathname, String fileName,String originfilename){
            boolean flag = false;
            InputStream inputStream = null;
            try{
                System.out.println("��ʼ�ϴ��ļ�");
                inputStream = new FileInputStream(new File(originfilename));
                initFtpClient();
                ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
                CreateDirecroty(pathname);
                //ftpClient.makeDirectory(pathname);
                ftpClient.changeWorkingDirectory(pathname);
                ftpClient.storeFile(fileName, inputStream);
                inputStream.close();
                ftpClient.logout();
                flag = true;
                System.out.println("�ϴ��ļ��ɹ�");
            }catch (Exception e) {
                System.out.println("�ϴ��ļ�ʧ��");
                e.printStackTrace();
            }finally{
                if(ftpClient.isConnected()){ 
                    try{
                        ftpClient.disconnect();
                        
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                } 
                if(null != inputStream){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } 
                } 
            }
            return true;
        }
        /**
         * �ϴ��ļ�
         * @param pathname ftp���񱣴��ַ
         * @param fileName �ϴ���ftp���ļ���
         * @param inputStream �����ļ��� 
         * @return
         */
        public boolean uploadFile( String pathname, String fileName,InputStream inputStream){
            boolean flag = false;
            try{
                System.out.println("��ʼ�ϴ��ļ�");
                initFtpClient();
                ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
                CreateDirecroty(pathname);
                ftpClient.makeDirectory(pathname);
                ftpClient.changeWorkingDirectory(pathname);
                ftpClient.storeFile(fileName, inputStream);
                inputStream.close();
                ftpClient.logout();
                flag = true;
                System.out.println("�ϴ��ļ��ɹ�");
            }catch (Exception e) {
                System.out.println("�ϴ��ļ�ʧ��");
                e.printStackTrace();
            }finally{
                if(ftpClient.isConnected()){ 
                    try{
                        ftpClient.disconnect();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                } 
                if(null != inputStream){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } 
                } 
            }
            return true;
        }
        //�ı�Ŀ¼·��
         public boolean changeWorkingDirectory(String directory) {
                boolean flag = true;
                try {
                    flag = ftpClient.changeWorkingDirectory(directory);
                    if (flag) {
                      System.out.println("�����ļ���" + directory + " �ɹ���");

                    } else {
                        System.out.println("�����ļ���" + directory + " ʧ�ܣ���ʼ�����ļ���");
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                return flag;
            }

        //�������Ŀ¼�ļ��������ftp�������Ѵ��ڸ��ļ����򲻴���������ޣ��򴴽�
        public boolean CreateDirecroty(String remote) throws IOException {
            boolean success = true;
            String directory = remote + "/";
            // ���Զ��Ŀ¼�����ڣ���ݹ鴴��Զ�̷�����Ŀ¼
            if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory))) {
                int start = 0;
                int end = 0;
                if (directory.startsWith("/")) {
                    start = 1;
                } else {
                    start = 0;
                }
                end = directory.indexOf("/", start);
                String path = "";
                String paths = "";
                while (true) {
                    String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
                    path = path + "/" + subDirectory;
                    if (!existFile(path)) {
                        if (makeDirectory(subDirectory)) {
                            changeWorkingDirectory(subDirectory);
                        } else {
                            System.out.println("����Ŀ¼[" + subDirectory + "]ʧ��");
                            changeWorkingDirectory(subDirectory);
                        }
                    } else {
                        changeWorkingDirectory(subDirectory);
                    }

                    paths = paths + "/" + subDirectory;
                    start = end + 1;
                    end = directory.indexOf("/", start);
                    // �������Ŀ¼�Ƿ񴴽����
                    if (end <= start) {
                        break;
                    }
                }
            }
            return success;
        }

      //�ж�ftp�������ļ��Ƿ����    
        public boolean existFile(String path) throws IOException {
                boolean flag = false;
                FTPFile[] ftpFileArr = ftpClient.listFiles(path);
                if (ftpFileArr.length > 0) {
                    flag = true;
                }
                return flag;
            }
        //����Ŀ¼
        public boolean makeDirectory(String dir) {
            boolean flag = true;
            try {
                flag = ftpClient.makeDirectory(dir);
                if (flag) {
                    System.out.println("�����ļ���" + dir + " �ɹ���");

                } else {
                    System.out.println("�����ļ���" + dir + " ʧ�ܣ�");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return flag;
        }
        
        /** * �����ļ� * 
        * @param pathname FTP�������ļ�Ŀ¼ * 
        * @param filename �ļ����� * 
        * @param localpath ���غ���ļ�·�� * 
        * @return */
        public  boolean downloadFile(String pathname, String filename, String localpath){ 
            boolean flag = false; 
            OutputStream os=null;
            try { 
                System.out.println("��ʼ�����ļ�");
                initFtpClient();
                //�л�FTPĿ¼ 
                ftpClient.changeWorkingDirectory(pathname); 
                FTPFile[] ftpFiles = ftpClient.listFiles(); 
                System.out.println(ftpFiles);
                for(FTPFile file : ftpFiles){ System.out.println(file.getName()+file.getType());}
                for(FTPFile file : ftpFiles){ 
                    if(filename.equalsIgnoreCase(file.getName())){ 
                        File localFile = new File(localpath + "/" + file.getName()); 
                        os = new FileOutputStream(localFile); 
                        ftpClient.retrieveFile(file.getName(), os); 
                        os.close(); 
                    } 
                } 
                ftpClient.logout(); 
                flag = true; 
                System.out.println("�����ļ��ɹ�");
            } catch (Exception e) { 
                System.out.println("�����ļ�ʧ��");
                e.printStackTrace(); 
            } finally{ 
                if(ftpClient.isConnected()){ 
                    try{
                        ftpClient.disconnect();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                } 
                if(null != os){
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } 
                } 
            } 
            return flag; 
        }
        
        /** * ɾ���ļ� * 
        * @param pathname FTP����������Ŀ¼ * 
        * @param filename Ҫɾ�����ļ����� * 
        * @return */ 
        public boolean deleteFile(String pathname, String filename){ 
            boolean flag = false; 
            try { 
                System.out.println("��ʼɾ���ļ�");
                initFtpClient();
                //�л�FTPĿ¼ 
                ftpClient.changeWorkingDirectory(pathname); 
                ftpClient.dele(filename); 
                ftpClient.logout();
                flag = true; 
                System.out.println("ɾ���ļ��ɹ�");
            } catch (Exception e) { 
                System.out.println("ɾ���ļ�ʧ��");
                e.printStackTrace(); 
            } finally {
                if(ftpClient.isConnected()){ 
                    try{
                        ftpClient.disconnect();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                } 
            }
            return flag; 
        }
        public String nowDir() throws Exception {
       
        
			return ftpClient.printWorkingDirectory();
			
		}
        public boolean showdir(ArrayList<String> dir,ArrayList<String>file,String path)  {
        	
        	
        	try {
        		
        		
        		ftpClient.changeWorkingDirectory(path); 
            FTPFile[] ftpFiles = ftpClient.listFiles(); 
            System.out.println(ftpFiles);
        
            for(FTPFile content : ftpFiles){ 
            	//System.out.println(content.getName()+content.getType());
            	if(content.getType()==0) {
            		dir.add(content.getName());
            		
            	}else {
            		file.add(content.getName());
            		
            		}
            	
            }
				return true;
			} catch (Exception e) {
				// TODO: handle exception
				return false;
			}
   
        	
		}
        
        public boolean removeDirectoryALLFile(String pathName) {
        	initFtpClient();
        	try {
	        	int replyCode = ftpClient.getReplyCode();
	        	if(!FTPReply.isPositiveCompletion(replyCode))
	        		{return false;
	        		}
	        	FTPFile[] files = ftpClient.listFiles(pathName);
	
	        	if (null != files && files.length > 0) {
	        		for (FTPFile file : files) {
		        		if (file.isDirectory()) {
		        			System.out.println(pathName + "/" + file.getName());
			        		removeDirectoryALLFile(pathName + "/" + file.getName() );
			
			        		                        // �л�����Ŀ¼����Ȼɾ�����ļ���
			        		ftpClient.changeWorkingDirectory(pathName.substring(0, pathName.lastIndexOf("/")));
			
			        		ftpClient.removeDirectory(pathName);
					        } else {
				        		if (!ftpClient.deleteFile(pathName + "/" + file.getName())) {
				        			return false;
				        			}
				        		}
			        	}  
	        		}
	
	            // �л�����Ŀ¼����Ȼɾ�����ļ���
	        	ftpClient.changeWorkingDirectory( pathName.substring(0, pathName.lastIndexOf("/")) );
	        	ftpClient.removeDirectory(pathName);
        		}   
        	catch (IOException e)    
	        	{
	        	e.printStackTrace();
	        	}

        	return true;
			
		}
        
        
        

        
        

		public static void main(String[] args) throws IOException {
            //FtpUtils ftp =new FtpUtils(); 
            String contentString ="test";
            //ftp.uploadFile("yls\\file", "ftpfile.txt", "E:\\ftpfile\\ftpfile.txt");//�ϴ� 
            //ftp.downloadFile("ftpFile/data", "123.docx", "F://");
            //ftp.deleteFile("ftp", "ftpfile.txt");
           // System.out.println("ok");
          
            //ftp.deleteFile("ftp", "del.doc");//ɾ�� 
            
           // FtpUtils ftp =new FtpUtils();
           // ftp.initFtpClient();
            //ftp.downloadFile("", "download.txt", "E:\\ftpfile");//����	
            
            //ftp.removeDirectoryALLFile("/delete");
            
            //ArrayList<String> dirArrayList=new ArrayList<String>();
            //ArrayList<String> fileArrayList=new ArrayList<String>();          
            //ftp.showdir(dirArrayList, fileArrayList, "yls");
           // System.out.println(dirArrayList);
            //System.out.println(fileArrayList);
            


            
            
            
            
         // ���� JFrame ʵ��
            JFrame frame = new JFrame("Login");
            // Setting the width and height of frame
            frame.setSize(800, 400);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            /* ������壬��������� HTML �� div ��ǩ
             * ���ǿ��Դ��������岢�� JFrame ��ָ��λ��
             * ��������ǿ�������ı��ֶΣ���ť�����������
             */
            JPanel panel = new JPanel();    
            // ������
            frame.add(panel);
            /* 
             * �����û�����ķ����������������
             */
            placeComponents(panel,frame);

            // ���ý���ɼ�
            frame.setVisible(true);
            
        }

        
        private static void placeComponents(JPanel panel,JFrame frame) {


            panel.setLayout(null);


            // ���� JLabel
            JLabel userLabel = new JLabel("User:");
            /* ������������������λ�á�
             * setBounds(x, y, width, height)
             * x �� y ָ�����Ͻǵ���λ�ã��� width �� height ָ���µĴ�С��
             */
            userLabel.setBounds(80,20,80,25);
            panel.add(userLabel);

            /* 
             * �����ı��������û�����
             */
            JTextField userText = new JTextField(20);
            userText.setBounds(160,20,165,25);

            userText.getText();
            panel.add(userText);

            // ����������ı���
            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setBounds(80,50,80,25);
            panel.add(passwordLabel);

            /* 
             *�����������������ı���
             * �����������Ϣ���Ե�Ŵ��棬���ڰ�������İ�ȫ��
             */
            JPasswordField passwordText = new JPasswordField(20);
            passwordText.setBounds(160,50,165,25);
            panel.add(passwordText);

            // ������¼��ť
            JButton loginButton = new JButton("��¼");
            loginButton.setBounds(100, 80, 80, 25);  
            loginButton.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					//System.out.println("user"+userText.getText());
					//System.out.println("pass"+passwordText.getText());
					int flag=loginFile(userText.getText(),passwordText.getText(),false);
					if(flag==1) {
						createMframe();
						}else if(flag==-1){
							java.awt.Toolkit.getDefaultToolkit().beep();
							JOptionPane.showMessageDialog(null, "�˺Ż��������", "��¼ʧ��",JOptionPane.ERROR_MESSAGE);								
						}else {
							java.awt.Toolkit.getDefaultToolkit().beep();
							JOptionPane.showMessageDialog(null, "�˺Ż�����Ϊ��", "��¼ʧ��",JOptionPane.ERROR_MESSAGE);			
						}
					
		            // Setting the width and height of frame		            					
				}
			});
            panel.add(loginButton);
            
            
            JButton registerButton =new JButton("ע��");
            registerButton.setBounds(250,80,80,25);
            registerButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					createRframe(frame);
				}
			});
            panel.add(registerButton);
            
         
   
        }
        
//������
	 public static void createMframe() {
		 JFrame mainframe = new JFrame("FTTTppp");
		 FtpUtils ftp =new FtpUtils();
		 
         mainframe.setSize(800, 400);
         mainframe.setLocationRelativeTo(null);
         JPanel panel = new JPanel(); 
 
         
         
         
         // ������

         //panel2.setVisible(false);
         panel.setLayout(null);
         JLabel hostLabel = new JLabel("Hostname:");
         hostLabel.setBounds(10,20,80,20);
         panel.add(hostLabel);
         
         
         panel.setVisible(true);
         //hostname
         JTextField hostTextField =new JTextField(15);
         hostTextField.setBounds(80,20,80,20);
         hostTextField.setText("127.0.0.1");         
         panel.add(hostTextField);
         
         JLabel userLabel = new JLabel("Username:");
         userLabel.setBounds(180,20,80,20);
         panel.add(userLabel);
         //user
         JTextField userTextField =new JTextField(15);
         userTextField.setBounds(250,20,80,20);
         userTextField.setText("admin");         
         panel.add(userTextField);
         
         JLabel psLabel = new JLabel("Password:");
         psLabel.setBounds(350,20,60,20);
         panel.add(psLabel);
         //password
         JTextField pawTextField =new JTextField(15);
         pawTextField.setBounds(450,20,80,20);
         pawTextField.setText("123");         
         panel.add(pawTextField);
         
         JLabel poLabel = new JLabel("Port:");
         poLabel.setBounds(550,20,30,20);
         panel.add(poLabel);

         //port
         JTextField poTextField =new JTextField(15);
         poTextField.setBounds(600,20,30,20);
         poTextField.setText("21");         
         panel.add(poTextField);
         JButton intoButton =new JButton("�����Ŀ¼");
         JButton downloadButton=new JButton("���ӷ�����");
         downloadButton.setBounds(650,20,150,20);
         downloadButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//panel.setVisible(false);
				//ftp.downloadFile("ftp", "download2.txt", "E:\\ftpfile");
				hostname=hostTextField.getText();
				port=Integer.parseInt(poTextField.getText());
				password=pawTextField.getText();
				username=userTextField.getText();
				 
				 
				if (ftp.initFtpClient()) {
					java.awt.Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "���ӳɹ�", "�ɹ�",JOptionPane.INFORMATION_MESSAGE);	
					intoButton.setVisible(true);
				}else {
					java.awt.Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "����ʧ��", "ʧ��",JOptionPane.ERROR_MESSAGE);	
				}
				
				
			}
		});
         panel.add(downloadButton);
         JLabel dirLabel = new JLabel("��ǰĿ¼���ļ���");
         dirLabel.setBounds(20,100,120,20);
         
         panel.add(dirLabel);
         JLabel fileLabel = new JLabel("��ǰĿ¼�ļ�");
         fileLabel.setBounds(250,100,80,20);      
         panel.add(fileLabel);
         
         //�ļ��б�
         JList<String> list=new JList<String>();
         //list.setBounds(180,120,180,180);
         panel.add(list);
         JScrollPane scrollPane =new JScrollPane();
         scrollPane.setBounds(250,120,230,200);
         panel.add(scrollPane);
         scrollPane.setViewportView(list);

         //�ļ����б�
         JList<String> list2=new JList<String>();
         //list.setBounds(180,120,180,180);
         panel.add(list2);
         JScrollPane scrollPane2 =new JScrollPane();
         scrollPane2.setBounds(20,120,200,200);
         panel.add(scrollPane2);
         scrollPane2.setViewportView(list2);
         
         JTextField pathField=new JTextField("");
         pathField.setBounds(20,50,180,20);
         panel.add(pathField);
         
         
         intoButton.setVisible(false);
         
         
         //����·��
         intoButton.setBounds(200,50,130,20);
         intoButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				intoButton.setText("����·��");
	            ArrayList<String> dirArrayList=new ArrayList<String>();
	            ArrayList<String> fileArrayList=new ArrayList<String>();   
	            
	            ftp.showdir(dirArrayList, fileArrayList, pathField.getText());
	            try {
					nowpath=ftp.nowDir();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            System.out.println(dirArrayList);
	            int size=dirArrayList.size(); 
	            String[] dirarray = (String[])dirArrayList.toArray(new String[size]);
	            	      
	            list.setListData(dirarray);
	            size=fileArrayList.size();
	            String[] filearray = (String[])fileArrayList.toArray(new String[size]);
	            list2.setListData(filearray);
	            System.out.println(fileArrayList);
			}
		});
         panel.add(intoButton);
         
         JButton flashButton=new JButton("ˢ��");
         flashButton.setBounds(470,50,130,20);
         flashButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ftp.initFtpClient();
				ftp.changeWorkingDirectory(nowpath);
	            ArrayList<String> dirArrayList=new ArrayList<String>();
	            ArrayList<String> fileArrayList=new ArrayList<String>();   
	            
	            try {
					ftp.showdir(dirArrayList, fileArrayList, ftp.nowDir());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            System.out.println(dirArrayList);
	            int size=dirArrayList.size(); 
	            String[] dirarray = (String[])dirArrayList.toArray(new String[size]);
	            	      
	            list.setListData(dirarray);
	            size=fileArrayList.size();
	            String[] filearray = (String[])fileArrayList.toArray(new String[size]);
	            list2.setListData(filearray);
	            System.out.println(fileArrayList);
			}
		});
         panel.add(flashButton);
         
         
         
         JButton backButton2 =new JButton("������һ��");
         backButton2.setBounds(350,50,100,20);
         panel.add(backButton2);

         
         backButton2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
	            ArrayList<String> dirArrayList=new ArrayList<String>();
	            ArrayList<String> fileArrayList=new ArrayList<String>();   
	            
	            ftp.showdir(dirArrayList, fileArrayList, "..");
	            System.out.println(dirArrayList);
	            int size=dirArrayList.size(); 
	            String[] dirarray = (String[])dirArrayList.toArray(new String[size]);
	            	      
	            list.setListData(dirarray);
	            size=fileArrayList.size();
	            String[] filearray = (String[])fileArrayList.toArray(new String[size]);
	            list2.setListData(filearray);
	            System.out.println(fileArrayList);
			
			}
		});
         //ɾ���ļ�
         JButton delButton=new JButton("ɾ����ѡ���ļ�");
         delButton.setBounds(550,180,150,20);
         panel.add(delButton);
         delButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					String delnameString=list.getSelectedValue();
					ftp.deleteFile(ftp.nowDir(), delnameString);
					ftp.initFtpClient();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
         
         //�ϴ�
         JButton upButton=new JButton("�ϴ��ļ�");
         upButton.setBounds(550,150,150,20);
         panel.add(upButton);
         upButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//ArrayList<String> filelist
				JFileChooser fc=new JFileChooser();
				FileSystemView fsv = FileSystemView .getFileSystemView();
		          fc.setCurrentDirectory(fsv.getHomeDirectory());
		         fc.setFileSelectionMode(JFileChooser.FILES_ONLY);//ֻ��ѡ���ļ�
		         String path=null;
		         String name=null;
		         File f=null;
		         int flag = 0;
				try{
		         flag=fc.showOpenDialog(null);
		         }
		         catch(HeadlessException head){
		         System.out.println("Open File Dialog ERROR!");
		         }
		         if(flag==JFileChooser.APPROVE_OPTION){
		         //��ø��ļ�
		         f=fc.getSelectedFile();
		         path=f.getPath();
		         name=f.getName();
		         }
		         System.out.print(name);
				String filename=list.getSelectedValue();
				try {
					//ftp.downloadFile(ftp.nowDir(), filename, path);
					nowpath=ftp.nowDir();
					ftp.uploadFile(ftp.nowDir(), name, path);//�ϴ� 
					ftp.initFtpClient();
					
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
         
         JTextField dirpathField =new JTextField();
         dirpathField.setBounds(500,210,130,20);
         panel.add(dirpathField);
         
         JButton delDirButton=new  JButton("ɾ����ѡ���ļ���");
         delDirButton.setBounds(550,240,130,20);
         panel.add(delDirButton);
         delDirButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String delpathString="/"+list2.getSelectedValue();
				try {
					System.out.print(ftp.nowDir()+delpathString);
					nowpath=ftp.nowDir();
					ftp.removeDirectoryALLFile(ftp.nowDir()+delpathString);
					//System.out.print(ftp.nowDir()+delpathString);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} );
         
         
         //�����ļ���
         JButton createButton=new JButton("�½��ļ���");
         createButton.setBounds(640,210,130,20);
         panel.add(createButton);
         createButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stubsdf
				String filepathString;
				if(dirpathField.getText().length()!=0) {
					try {
						filepathString = ftp.nowDir()+"/"+dirpathField.getText();
						ftp.initFtpClient();
						System.out.print(filepathString);
						ftp.CreateDirecroty(filepathString);
						ftp.changeWorkingDirectory("..");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
				}else {
					java.awt.Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "���ֲ���Ϊ��", "ʧ��",JOptionPane.ERROR_MESSAGE);	
					
				}
			}
		});
         
         
         
         //�����ļ�
         JButton downButton=new JButton("������ѡ���ļ�");
         downButton.setBounds(550,120,150,20);
         panel.add(downButton);
         downButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//ArrayList<String> filelist
				JFileChooser fc=new JFileChooser();
				FileSystemView fsv = FileSystemView .getFileSystemView();
		          fc.setCurrentDirectory(fsv.getHomeDirectory());
		         fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//ֻ��ѡ��Ŀ¼
		         String path=null;
		         File f=null;
		         int flag = 0;
				try{
		         flag=fc.showOpenDialog(null);
		         }
		         catch(HeadlessException head){
		         System.out.println("Open File Dialog ERROR!");
		         }
		         if(flag==JFileChooser.APPROVE_OPTION){
		         //��ø��ļ�
		         f=fc.getSelectedFile();
		         path=f.getPath();
		         }
		         System.out.print(path);
				String filename=list.getSelectedValue();
				try {
					nowpath=ftp.nowDir();
					ftp.downloadFile(ftp.nowDir(), filename, path);
					ftp.initFtpClient();
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
 
         
         
         
         
         
         
         
         mainframe.add(panel);
         
         mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         // ���ý���ɼ�
         mainframe.setVisible(true);
	}
	 
	 //��ע�����
	 public static void createRframe(JFrame frame) {
		 JFrame  rFrame= new JFrame("ע��");
		 rFrame.setSize(800, 400);
		 rFrame.setLocationRelativeTo(null);
		 JPanel panel = new JPanel(); 
		 rFrame.add(panel);
		
		 //��ȡ����
		panel.setLayout(null);
        JLabel passwordLabel = new JLabel("��������:");
        passwordLabel.setBounds(80,50,80,25);
        panel.add(passwordLabel);
        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(160,50,165,25);
        panel.add(passwordText);
        JLabel passwordLabel2 = new JLabel("�ٴ���������:");
        passwordLabel2.setBounds(80,80,80,25);
        panel.add(passwordLabel2);
        JPasswordField passwordText2 = new JPasswordField(20);
        passwordText2.setBounds(160,80,165,25);
        panel.add(passwordText2);
        
        //��ȡ�˺�
        JLabel userLabel = new JLabel("�����˻���:");
        /* ������������������λ�á�
         * setBounds(x, y, width, height)
         * x �� y ָ�����Ͻǵ���λ�ã��� width �� height ָ���µĴ�С��
         */
        userLabel.setBounds(80,20,80,25);
        panel.add(userLabel);

        /* 
         * �����ı��������û�����
         */
        JTextField userText = new JTextField(20);
        userText.setBounds(160,20,165,25);

        userText.getText();
        panel.add(userText);
        
        JButton reButton =new JButton("ȷ��");
        reButton.setBounds(100,150,100,25);
        reButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (!passwordText.getText().equals(passwordText2.getText())) {
					java.awt.Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "�������벻ͬ", "ע��ʧ��",JOptionPane.ERROR_MESSAGE);	
					
				}else {
					if(loginFile(userText.getText(), passwordText.getText(), true)==4) 
					{
						java.awt.Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(null, "�˺��Ѵ���", "ע��ʧ��",JOptionPane.ERROR_MESSAGE);	
						
						
					}
					if(loginFile(userText.getText(), passwordText.getText(), true)==0) {
						java.awt.Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(null, "�˺Ż����벻��Ϊ��", "ע��ʧ��",JOptionPane.ERROR_MESSAGE);	
						
					}
					if(loginFile(userText.getText(), passwordText.getText(), true)==5){
						
						 wrFile(userText.getText(), passwordText.getText());
						 frame.setVisible(true); //������¼
						 rFrame.setVisible(false);	//�ر�ע��
					}
				}
				//frame.setVisible(true);
			}
		});
        
        panel.add(reButton);
        
        JButton backButton =new JButton("�ص���¼");
        backButton.setBounds(230,150,100,25);
        backButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				// TODO Auto-generated method stub
				 frame.setVisible(true); //������¼
				 rFrame.setVisible(false);	//�ر�ע��
			}
		});
        
        panel.add(backButton);
        
        
        
		 rFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         // ���ý���ɼ�
		 rFrame.setVisible(true);		
	}
	 
	 
	 //�ж��˺������Ƿ���ȷ,����
     public static int loginFile(String username, String PSW,Boolean test){ 
    	 
    	 if(username.equals("")||PSW.equals("")) {
    		 
    		 return 0;
    	 }
    	 
     	String loginInfo=username+"_"+PSW;
     	System.out.println(loginInfo);
     	String ALLinfo;
     	File file =new File("E:\\ftpfile\\info.txt");
 		if(file.exists()){
 			try {        			
 					InputStream input=new FileInputStream(file);
	    			byte[] data=new byte[1024];
	    			int len=input.read(data);
	    			System.out.println(data);
	    			ALLinfo=new String(data,0,len);
	    			System.out.println(ALLinfo); //�� ��0��ʼ���� len
	    			if(test) {
	    				String user_=username+"_";
	    				if(ALLinfo.indexOf(user_)!=-1) {
	    					return 4;
	    				}else {return 5;}
	    				
	    			}
	    			
	    			if(ALLinfo.indexOf(loginInfo)!=-1) {
	    				return 1;
	    			}else {
						return -1;
					}



				} catch (IOException e) {
					// TODO: handle exception
					 e.printStackTrace();
					
				}
     	
 		}
		return 0;

 	}
     //����ע����Ϣ
     public static void wrFile(String username, String PSW){ 
     	String contentString=username+"_"+PSW+",";
     	File file =new File("E:\\ftpfile\\info.txt");
 		if(file.exists()){
 			try {

     			OutputStream ouput =new FileOutputStream(file,true); //׷��
     			ouput.write(contentString.getBytes());
     			ouput.flush();
     			ouput.close();
     			
				} catch (IOException e) {
					// TODO: handle exception
					 e.printStackTrace();
					
				}
     	
 		}
 	}
}




