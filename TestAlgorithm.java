import java.io.*;
import java.util.*;

public class TestAlgorithm {
	public static void main(String[] args) throws Exception {
		try {
			String resource_file = "d:/java/OS/MyFile/Resource.txt";
			String claim_file = "d:/java/OS/MyFile/Claim.txt";
			String alloc_file = "d:/java/OS/MyFile/Allocation.txt";
			String[] resource_split = getSplit(resource_file);
			String[] claim_split = getSplit(claim_file);
			String[] alloc_split = getSplit(alloc_file);
			int[][] resource = new int[resource_split.length][];
			int[][] claim = new int[claim_split.length][];
			int[][] alloc = new int[alloc_split.length][];
			GetArraysFromFile gaff = new GetArraysFromFile();
			List<Integer> waitList = new ArrayList<Integer>();
			for(int i=0; i<claim.length; i++) {
				waitList.add(new Integer(i));	
			}
			System.out.println("当前系统内等待资源的进程有：");
			for(int i=0; i<waitList.size(); i++) {
						System.out.println((waitList.get(i)).intValue() + "号进程");	
			}
			System.out.println("总资源：");
			resource = gaff.getArrays(resource_split, resource_file);
			System.out.println("\n");
			System.out.println("各进程所需资源：");
			System.out.printf("     ");
			for(int i=0; i<resource[0].length; i++) {
				System.out.printf("R%d     ", i);
			}
			System.out.println();
			claim = gaff.getArrays(claim_split, claim_file);
			System.out.println("\n");
			System.out.println("各进程当前所分配的资源：");
			System.out.printf("     ");
			for(int i=0; i<resource[0].length; i++) {
				System.out.printf("R%d     ", i);
			}
			System.out.println();
			alloc = gaff.getArrays(alloc_split, alloc_file);
			System.out.println("\n");
			BankerAlgorithm ba = new BankerAlgorithm();
			ba.resourceAlloc(resource, claim, alloc);
		} catch (FileNotFoundException e) {
				System.out.println("The file can not be found!");
				e.printStackTrace();
			} catch(IOException e) {
					System.out.println("There is an IO error!");
					e.printStackTrace();
				}
	}
		
	public static String[] getSplit(String myFile) throws Exception {
		File file = new File(myFile) ;
		InputStream input = new FileInputStream(file) ;
		byte[] b = new byte[(int)file.length()] ;
		input.read(b) ;
		
		String str = new String(b) ;
		String[] split = str.split("\r\n") ;
		input.close() ;
		return split;
	}
}

	/*
class GetArraysFromFile {
	private int i;
	private int j;
	public int[][] getArrays(String myFile) {
		  BufferedReader br = new BufferedReader(new FileReader(file));
			StringTokenizer st = null;
		  String temp = "";
			while(br.ready()) {
				j = 0;
				String s = null;
				temp = br.readLine();
				if(temp != null && temp != "") { 
					st = new StringTokenizer(temp);
					while(st.hasMoreTokens()) {
						s = st.nextToken();
						System.out.println(s);
						array[i][j] = Integer.parseInt(st.nextToken());
						j++;
					}
				}
				i++;
			}
		} catch(NullPointerException e) {
				System.out.println("The str is null");
				e.printStackTrace();
			} catch(FileNotFoundException e) {
				System.out.println("File can not be found!");
				e.printStackTrace();
				} catch(IOException e) {
					e.printStackTrace();
					}
					
	}
	*/	

class GetArraysFromFile {
	public int[][] getArrays(String[] split, String myFile) throws Exception {
			String[][] array = new String[split.length][] ;
			for(int i=0;i<split.length;i++){
				array[i] = split[i].split(" ") ; 
			}
			int[][] temp = new int[split.length][array[0].length] ;
			for(int i=0;i<temp.length;i++){
				if(temp.length != 1)
				  System.out.printf("P%d:  ", i);
				for(int j=0;j<temp[0].length;j++){
					temp[i][j] = Integer.parseInt(array[i][j]) ;
					System.out.printf(temp[i][j]+"      ") ;
				}
				System.out.println();
			}
			return temp; 
		}
}


class BankerAlgorithm {
		private int[][] resource;
		private int[][] claim;
		private int[][] alloc;
		private int[] available;
//List<Integer> rest = new ArrayList<Integer>();          注意！！rest必须为safe的局部变量！！
		List<Integer> safeOrder = new ArrayList<Integer>();
		List<Integer> suspendList = new ArrayList<Integer>();
		List<Integer> waitList = new ArrayList<Integer>();
		private boolean b = true;
		
		public void resourceAlloc(int[][] resource, int[][] claim, int[][] alloc) {
			this.resource = resource;
			this.claim = claim;
			this.alloc = alloc;
			boolean flag = true;
			int number = claim.length;
			int[] available = new int[resource[0].length];
			for(int i=0; i<claim.length; i++) {
				waitList.add(new Integer(i));	
			}
			for(int i=0; i<resource[0].length; i++) {
				int sum = 0;
				for(int j=0; j<alloc.length; j++) {
					sum = sum + alloc[j][i];
				}
				available[i] = resource[0][i] - sum;
			}
			while(b && number>0) {
				try {
					Scanner input = new Scanner(System.in);
					System.out.println("当前系统内等待资源的进程有：");
					for(int i=0; i<waitList.size(); i++) {
						System.out.println((waitList.get(i)).intValue() + "号进程");	
					}
					System.out.println();
					System.out.printf("请输入请求资源的进程ID（若要结束测试，则输入-1）： ");
					int p = input.nextInt();
					if(p == -1) {b = false;System.out.println("exit!");}
					else {
						boolean judge = false;
						for(int i=0; i<waitList.size(); i++) {
							if(p == (waitList.get(i)).intValue())  judge = true;
						}
						while(p >= claim.length || !judge) {
							System.out.println("没有该进程！请重新输入!");
							p = input.nextInt();
							for(int i=0; i<waitList.size(); i++) {
								if(p == (waitList.get(i)).intValue())  judge = true;
							}
							System.out.println();
						}
						System.out.printf("当前进程总共所需资源数为%d个\n\n请输入所请求的每个资源的个数： ", claim[p].length);
						System.out.println();
						int request[] = new int[claim[p].length];
						for(int i=0; i<claim[p].length; i++) {
							System.out.printf("R%d: ", i);
							request[i] = input.nextInt();	
						} 
						for(int i=0; i<claim[p].length; i++) {
							if(alloc[p][i] + request[i] > claim[p][i])  {
								flag = false; break;	
							}	
						}
						if(!flag) {
							System.out.println("错误：请求越界！请重新请求资源");
							System.out.println();
							flag = true;
						}
						else {
							for(int i=0; i<available.length; i++) {
								if(request[i] > available[i]) {
									flag = false; break;	
								}	
							}
							if(!flag) {
								System.out.println("当前可用资源不足！请选择其他进程");
								System.out.println();
								suspendList.add(new Integer(p));	
								flag = true;
							}
							else {
//System.out.println("!!!");
//System.out.println(safeOrder.size());
								for(int i=0; i<alloc[p].length; i++) {
									alloc[p][i] += request[i];
									available[i] -= request[i];	
								}	
						 
						State s = new State();
						s.initialize(resource, claim, alloc, available);
						if(safe(s)) {
//System.out.println("!!!");
//System.out.println(safeOrder.size());
						System.out.println("资源请求成功！安全分配的顺序为： ");
						boolean remove_condition = true;
						for(int i=0; i<claim[0].length; i++) {
							if(claim[p][i] - alloc[p][i] != 0) {
								remove_condition = false;
							}
						}
						if(remove_condition) {
							waitList.remove(new Integer(p));
							number--;
						}
							for(int i=0; i<claim[0].length; i++) {
							 claim[p][i] = -1;
							 alloc[p][i] = -1;	
						 }
						for(int i=0; i<safeOrder.size(); i++) {
							System.out.println((safeOrder.get(i)).intValue());
						}
						safeOrder.clear();
						System.out.println();
						System.out.println("请继续操作。");
						}
						else {
							for(int i=0; i<alloc[p].length; i++) {
								alloc[p][i] -= request[i];
								available[i] += request[i];	
							}
							suspendList.add(new Integer(p));
							System.out.println("该请求不安全，拒绝分配。请选择其他进程");
							safeOrder.clear();
							Iterator k = safeOrder.iterator();
							System.out.println(k.hasNext());
						}
					}
				}	
			}
		} catch(NoSuchElementException e) {
					System.out.println("请输入整型值！");
					System.out.println();
			 	}
			}
			if(b) {
				System.out.println("所有进程资源请求成功，运行完毕！");	
			}
		}
	
	public boolean safe(State s) {
		List<Integer> rest = new ArrayList<Integer>();
		int flag = 0;
		boolean found = true;
		int[] currentavail = s.getAvailable();
		for(int i=0; i<claim.length; i++) {
			if(claim[i][0] != -1)
				rest.add(new Integer(i));	
		}
		boolean possible = true;
		while(possible) {
			Iterator i = rest.iterator();
			while(i.hasNext()) {
				found = true;
				flag = ((Integer)i.next()).intValue();
				for(int j=0; j<currentavail.length; j++) {
					if(claim[flag][j] - alloc[flag][j] > currentavail[j]) {
						found = false; break;
					}
				}
				if(!found) {
					continue;
				}
				else break;
			}
			if(found && rest.size() != 0) {
				for(int j=0; j<currentavail.length; j++) {
					currentavail[j] += claim[flag][j];	
				}
				rest.remove(new Integer(flag));
				safeOrder.add(new Integer(flag));
			}
			else {
				possible = false;	
			}	
		}
		return (rest.size() == 0);
	}	
		
}

class State {
	private int numberOfProcess;
	private int[][] resource;
	private int[][] claim;
	private int[][] alloc;
	private int[] available;
	
	public void initialize(int[][] resource, int[][] claim, int[][] alloc, int[] available) {
		this.	resource = resource;
		this.claim = claim;
		this.alloc = alloc;
		this.available = available;
	}
	
	public int[][] getResource() {
			return resource;
	}
	
	public int[][] getClaim() {
		return claim;	
	}
	
	public int[][] getAlloc() {
		return alloc;
	}	
	
	public int[] getAvailable() {
		return available;	
	}
	
	public int getNumberOfProcess() {
		return claim.length;	
	}
}