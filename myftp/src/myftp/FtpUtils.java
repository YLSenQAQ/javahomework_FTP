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
        //ftp服务器地址
        public static String hostname = "127.0.0.1";
        //ftp服务器端口号默认为21
        public static Integer port = 21 ;
        //ftp登录账号
        public static String username = "admin";
        //ftp登录密码
        public static String password = "123";
        public static String nowpath = "";
        
        public FTPClient ftpClient = null;
        
        /**
         * 初始化ftp服务器
         * @return 
         */
        public boolean initFtpClient() {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            try {
                System.out.println("connecting...ftp服务器:"+this.hostname+":"+this.port); 
                ftpClient.connect(hostname, port); //连接ftp服务器
                ftpClient.login(username, password); //登录ftp服务器
                int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
                if(!FTPReply.isPositiveCompletion(replyCode)){
                    System.out.println("connect failed...ftp服务器:"+this.hostname+":"+this.port);
                    return false;
                }
                System.out.println("connect successfu...ftp服务器:"+this.hostname+":"+this.port); 
                return true;
            }catch (MalformedURLException e) { 
               e.printStackTrace(); 
            }catch (IOException e) { 
               e.printStackTrace(); 
            } 
            return false;
        }

        /**
        * 上传文件
        * @param pathname ftp服务保存地址
        * @param fileName 上传到ftp的文件名
        *  @param originfilename 待上传文件的名称（绝对地址） * 
        * @return
        */
        public boolean uploadFile( String pathname, String fileName,String originfilename){
            boolean flag = false;
            InputStream inputStream = null;
            try{
                System.out.println("开始上传文件");
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
                System.out.println("上传文件成功");
            }catch (Exception e) {
                System.out.println("上传文件失败");
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
         * 上传文件
         * @param pathname ftp服务保存地址
         * @param fileName 上传到ftp的文件名
         * @param inputStream 输入文件流 
         * @return
         */
        public boolean uploadFile( String pathname, String fileName,InputStream inputStream){
            boolean flag = false;
            try{
                System.out.println("开始上传文件");
                initFtpClient();
                ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
                CreateDirecroty(pathname);
                ftpClient.makeDirectory(pathname);
                ftpClient.changeWorkingDirectory(pathname);
                ftpClient.storeFile(fileName, inputStream);
                inputStream.close();
                ftpClient.logout();
                flag = true;
                System.out.println("上传文件成功");
            }catch (Exception e) {
                System.out.println("上传文件失败");
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
        //改变目录路径
         public boolean changeWorkingDirectory(String directory) {
                boolean flag = true;
                try {
                    flag = ftpClient.changeWorkingDirectory(directory);
                    if (flag) {
                      System.out.println("进入文件夹" + directory + " 成功！");

                    } else {
                        System.out.println("进入文件夹" + directory + " 失败！开始创建文件夹");
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                return flag;
            }

        //创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
        public boolean CreateDirecroty(String remote) throws IOException {
            boolean success = true;
            String directory = remote + "/";
            // 如果远程目录不存在，则递归创建远程服务器目录
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
                            System.out.println("创建目录[" + subDirectory + "]失败");
                            changeWorkingDirectory(subDirectory);
                        }
                    } else {
                        changeWorkingDirectory(subDirectory);
                    }

                    paths = paths + "/" + subDirectory;
                    start = end + 1;
                    end = directory.indexOf("/", start);
                    // 检查所有目录是否创建完毕
                    if (end <= start) {
                        break;
                    }
                }
            }
            return success;
        }

      //判断ftp服务器文件是否存在    
        public boolean existFile(String path) throws IOException {
                boolean flag = false;
                FTPFile[] ftpFileArr = ftpClient.listFiles(path);
                if (ftpFileArr.length > 0) {
                    flag = true;
                }
                return flag;
            }
        //创建目录
        public boolean makeDirectory(String dir) {
            boolean flag = true;
            try {
                flag = ftpClient.makeDirectory(dir);
                if (flag) {
                    System.out.println("创建文件夹" + dir + " 成功！");

                } else {
                    System.out.println("创建文件夹" + dir + " 失败！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return flag;
        }
        
        /** * 下载文件 * 
        * @param pathname FTP服务器文件目录 * 
        * @param filename 文件名称 * 
        * @param localpath 下载后的文件路径 * 
        * @return */
        public  boolean downloadFile(String pathname, String filename, String localpath){ 
            boolean flag = false; 
            OutputStream os=null;
            try { 
                System.out.println("开始下载文件");
                initFtpClient();
                //切换FTP目录 
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
                System.out.println("下载文件成功");
            } catch (Exception e) { 
                System.out.println("下载文件失败");
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
        
        /** * 删除文件 * 
        * @param pathname FTP服务器保存目录 * 
        * @param filename 要删除的文件名称 * 
        * @return */ 
        public boolean deleteFile(String pathname, String filename){ 
            boolean flag = false; 
            try { 
                System.out.println("开始删除文件");
                initFtpClient();
                //切换FTP目录 
                ftpClient.changeWorkingDirectory(pathname); 
                ftpClient.dele(filename); 
                ftpClient.logout();
                flag = true; 
                System.out.println("删除文件成功");
            } catch (Exception e) { 
                System.out.println("删除文件失败");
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
			
			        		                        // 切换到父目录，不然删不掉文件夹
			        		ftpClient.changeWorkingDirectory(pathName.substring(0, pathName.lastIndexOf("/")));
			
			        		ftpClient.removeDirectory(pathName);
					        } else {
				        		if (!ftpClient.deleteFile(pathName + "/" + file.getName())) {
				        			return false;
				        			}
				        		}
			        	}  
	        		}
	
	            // 切换到父目录，不然删不掉文件夹
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
            //ftp.uploadFile("yls\\file", "ftpfile.txt", "E:\\ftpfile\\ftpfile.txt");//上传 
            //ftp.downloadFile("ftpFile/data", "123.docx", "F://");
            //ftp.deleteFile("ftp", "ftpfile.txt");
           // System.out.println("ok");
          
            //ftp.deleteFile("ftp", "del.doc");//删除 
            
           // FtpUtils ftp =new FtpUtils();
           // ftp.initFtpClient();
            //ftp.downloadFile("", "download.txt", "E:\\ftpfile");//下载	
            
            //ftp.removeDirectoryALLFile("/delete");
            
            //ArrayList<String> dirArrayList=new ArrayList<String>();
            //ArrayList<String> fileArrayList=new ArrayList<String>();          
            //ftp.showdir(dirArrayList, fileArrayList, "yls");
           // System.out.println(dirArrayList);
            //System.out.println(fileArrayList);
            


            
            
            
            
         // 创建 JFrame 实例
            JFrame frame = new JFrame("Login");
            // Setting the width and height of frame
            frame.setSize(800, 400);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            /* 创建面板，这个类似于 HTML 的 div 标签
             * 我们可以创建多个面板并在 JFrame 中指定位置
             * 面板中我们可以添加文本字段，按钮及其他组件。
             */
            JPanel panel = new JPanel();    
            // 添加面板
            frame.add(panel);
            /* 
             * 调用用户定义的方法并添加组件到面板
             */
            placeComponents(panel,frame);

            // 设置界面可见
            frame.setVisible(true);
            
        }

        
        private static void placeComponents(JPanel panel,JFrame frame) {


            panel.setLayout(null);


            // 创建 JLabel
            JLabel userLabel = new JLabel("User:");
            /* 这个方法定义了组件的位置。
             * setBounds(x, y, width, height)
             * x 和 y 指定左上角的新位置，由 width 和 height 指定新的大小。
             */
            userLabel.setBounds(80,20,80,25);
            panel.add(userLabel);

            /* 
             * 创建文本域用于用户输入
             */
            JTextField userText = new JTextField(20);
            userText.setBounds(160,20,165,25);

            userText.getText();
            panel.add(userText);

            // 输入密码的文本域
            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setBounds(80,50,80,25);
            panel.add(passwordLabel);

            /* 
             *这个类似用于输入的文本域
             * 但是输入的信息会以点号代替，用于包含密码的安全性
             */
            JPasswordField passwordText = new JPasswordField(20);
            passwordText.setBounds(160,50,165,25);
            panel.add(passwordText);

            // 创建登录按钮
            JButton loginButton = new JButton("登录");
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
							JOptionPane.showMessageDialog(null, "账号或密码错误", "登录失败",JOptionPane.ERROR_MESSAGE);								
						}else {
							java.awt.Toolkit.getDefaultToolkit().beep();
							JOptionPane.showMessageDialog(null, "账号或密码为空", "登录失败",JOptionPane.ERROR_MESSAGE);			
						}
					
		            // Setting the width and height of frame		            					
				}
			});
            panel.add(loginButton);
            
            
            JButton registerButton =new JButton("注册");
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
        
//主界面
	 public static void createMframe() {
		 JFrame mainframe = new JFrame("FTTTppp");
		 FtpUtils ftp =new FtpUtils();
		 
         mainframe.setSize(800, 400);
         mainframe.setLocationRelativeTo(null);
         JPanel panel = new JPanel(); 
 
         
         
         
         // 添加面板

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
         JButton intoButton =new JButton("进入根目录");
         JButton downloadButton=new JButton("连接服务器");
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
					JOptionPane.showMessageDialog(null, "连接成功", "成功",JOptionPane.INFORMATION_MESSAGE);	
					intoButton.setVisible(true);
				}else {
					java.awt.Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "连接失败", "失败",JOptionPane.ERROR_MESSAGE);	
				}
				
				
			}
		});
         panel.add(downloadButton);
         JLabel dirLabel = new JLabel("当前目录下文件夹");
         dirLabel.setBounds(20,100,120,20);
         
         panel.add(dirLabel);
         JLabel fileLabel = new JLabel("当前目录文件");
         fileLabel.setBounds(250,100,80,20);      
         panel.add(fileLabel);
         
         //文件列表
         JList<String> list=new JList<String>();
         //list.setBounds(180,120,180,180);
         panel.add(list);
         JScrollPane scrollPane =new JScrollPane();
         scrollPane.setBounds(250,120,230,200);
         panel.add(scrollPane);
         scrollPane.setViewportView(list);

         //文件夹列表
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
         
         
         //进入路径
         intoButton.setBounds(200,50,130,20);
         intoButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				intoButton.setText("进入路径");
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
         
         JButton flashButton=new JButton("刷新");
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
         
         
         
         JButton backButton2 =new JButton("返回上一级");
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
         //删除文件
         JButton delButton=new JButton("删除已选的文件");
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
         
         //上传
         JButton upButton=new JButton("上传文件");
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
		         fc.setFileSelectionMode(JFileChooser.FILES_ONLY);//只能选择文件
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
		         //获得该文件
		         f=fc.getSelectedFile();
		         path=f.getPath();
		         name=f.getName();
		         }
		         System.out.print(name);
				String filename=list.getSelectedValue();
				try {
					//ftp.downloadFile(ftp.nowDir(), filename, path);
					nowpath=ftp.nowDir();
					ftp.uploadFile(ftp.nowDir(), name, path);//上传 
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
         
         JButton delDirButton=new  JButton("删除已选的文件夹");
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
         
         
         //创建文件夹
         JButton createButton=new JButton("新建文件夹");
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
					JOptionPane.showMessageDialog(null, "名字不能为空", "失败",JOptionPane.ERROR_MESSAGE);	
					
				}
			}
		});
         
         
         
         //下载文件
         JButton downButton=new JButton("下载已选的文件");
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
		         fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//只能选择目录
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
		         //获得该文件
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
         // 设置界面可见
         mainframe.setVisible(true);
	}
	 
	 //画注册界面
	 public static void createRframe(JFrame frame) {
		 JFrame  rFrame= new JFrame("注册");
		 rFrame.setSize(800, 400);
		 rFrame.setLocationRelativeTo(null);
		 JPanel panel = new JPanel(); 
		 rFrame.add(panel);
		
		 //获取密码
		panel.setLayout(null);
        JLabel passwordLabel = new JLabel("输入密码:");
        passwordLabel.setBounds(80,50,80,25);
        panel.add(passwordLabel);
        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(160,50,165,25);
        panel.add(passwordText);
        JLabel passwordLabel2 = new JLabel("再次输入密码:");
        passwordLabel2.setBounds(80,80,80,25);
        panel.add(passwordLabel2);
        JPasswordField passwordText2 = new JPasswordField(20);
        passwordText2.setBounds(160,80,165,25);
        panel.add(passwordText2);
        
        //获取账号
        JLabel userLabel = new JLabel("输入账户名:");
        /* 这个方法定义了组件的位置。
         * setBounds(x, y, width, height)
         * x 和 y 指定左上角的新位置，由 width 和 height 指定新的大小。
         */
        userLabel.setBounds(80,20,80,25);
        panel.add(userLabel);

        /* 
         * 创建文本域用于用户输入
         */
        JTextField userText = new JTextField(20);
        userText.setBounds(160,20,165,25);

        userText.getText();
        panel.add(userText);
        
        JButton reButton =new JButton("确定");
        reButton.setBounds(100,150,100,25);
        reButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (!passwordText.getText().equals(passwordText2.getText())) {
					java.awt.Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "两次密码不同", "注册失败",JOptionPane.ERROR_MESSAGE);	
					
				}else {
					if(loginFile(userText.getText(), passwordText.getText(), true)==4) 
					{
						java.awt.Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(null, "账号已存在", "注册失败",JOptionPane.ERROR_MESSAGE);	
						
						
					}
					if(loginFile(userText.getText(), passwordText.getText(), true)==0) {
						java.awt.Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(null, "账号或密码不能为空", "注册失败",JOptionPane.ERROR_MESSAGE);	
						
					}
					if(loginFile(userText.getText(), passwordText.getText(), true)==5){
						
						 wrFile(userText.getText(), passwordText.getText());
						 frame.setVisible(true); //开启登录
						 rFrame.setVisible(false);	//关闭注册
					}
				}
				//frame.setVisible(true);
			}
		});
        
        panel.add(reButton);
        
        JButton backButton =new JButton("回到登录");
        backButton.setBounds(230,150,100,25);
        backButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				// TODO Auto-generated method stub
				 frame.setVisible(true); //开启登录
				 rFrame.setVisible(false);	//关闭注册
			}
		});
        
        panel.add(backButton);
        
        
        
		 rFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         // 设置界面可见
		 rFrame.setVisible(true);		
	}
	 
	 
	 //判断账号密码是否正确,存在
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
	    			System.out.println(ALLinfo); //从 第0开始读到 len
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
     //保存注册信息
     public static void wrFile(String username, String PSW){ 
     	String contentString=username+"_"+PSW+",";
     	File file =new File("E:\\ftpfile\\info.txt");
 		if(file.exists()){
 			try {

     			OutputStream ouput =new FileOutputStream(file,true); //追加
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




