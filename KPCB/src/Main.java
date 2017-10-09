/*Name: Suraj Satish Patil
 *College: VTech
 *email:surajp9@vt.edu
 * Problem Statement: Implement fixed size Hash Map
 * Brief Solution walk through:
 * I have used the Double Hashing approach:
 * 1. Fibonacci Hashing
 * 2. Mod Hashing (Key mod Size)
 * The collisions are managed by open addressing approach where the offset is determined by the second hash function.
 * 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
		String cmd;
		try {
			HashMap map = null;
			boolean flag =true;
			System.out.println("For executing the program."
					+ "\nFollowing comands are supported:"
					+ "\n1. constructor <size>"
					+ "\n2. set <key> <value>"
					+ "\n3. get <key>"
					+ "\n4. delete <key>"
					+ "\n5. load"
					+ "\n6. stop");	
			while (flag && (cmd = bi.readLine()) != null) {
				String cmdArr[] = cmd.toLowerCase().trim().replaceAll("\\s+"," ").split(" ");
				switch(cmdArr[0])
				{
				case "constructor":
					if(2 == cmdArr.length) {
						try{
							Integer.parseInt(cmdArr[1]);
						}
						catch(NumberFormatException e) {
							System.out.println("Please provide a umber input with 'constructor' command");
							break;
						}
						map = new HashMap(Integer.parseInt(cmdArr[1]));
					}
					else {
						System.out.println("Incorrect input format for 'constructor' call"
								+ "\nPlease use the following syntax:  constructor <size>");
					}
					break;

				case "set":
					if(3 == cmdArr.length) {
						if(null == map)
						{
							System.out.println("Incorrect 'set' usage. Should use constructor command prior to set.");
							break;
						}
						boolean ans = map.set(cmdArr[1], cmdArr[2]);
						if(ans) {
							System.out.println("Success.");
						}
						else System.out.println("Failure.");					
					}
					else {
						System.out.println("Incorrect input format for 'set' call"
								+ "\n Please use the following syntax:  set <key> <value>");
					}
					break;

				case "get":
					if(2 == cmdArr.length) {
						if(null == map)
						{
							System.out.println("Incorrect 'get' usage. Should use constructor command prior to set.");
							break;
						}
						Object ans = map.get(cmdArr[1]);
						if(null == ans) {
							System.out.println("Failure.");
						}
						else System.out.println("Object value: "+ans);					
					}
					else {
						System.out.println("Incorrect input format for 'get' call"
								+ "\n Please use the following syntax:  get <key>");
					}
					break;
					
				case "delete":
					if(2 == cmdArr.length) {
						if(null == map)
						{
							System.out.println("Incorrect 'delete' usage. Should use constructor command prior to set.");
							break;
						}
						Object ans = map.delete(cmdArr[1]);
						if(null == ans) {
							System.out.println("Failure.");
						}
						else System.out.println("Object deleted: "+ans);					
					}
					else {
						System.out.println("Incorrect input format for 'delete' call"
								+ "\n Please use the following syntax:  delete <key>");
					}
					break;
					
				case "load":
					if(1 == cmdArr.length) {
						if(null == map)
						{
							System.out.println("Incorrect 'load' usage. Should use constructor command prior to set.");
							break;
						}
						Float ans = map.getLoad();
						System.out.println("Load factor: "+ans);					
					}
					else {
						System.out.println("Incorrect input format for 'load' call"
								+ "\n Please use the following syntax:  load");
					}
					break;
				case "stop":
					flag = false;
					break;
					
				default:
					System.out.println("Please enter valid input command."
							+ "\nFollowing comands are supported:"
							+ "\n1. constructor <size>"
							+ "\n2. set <key> <value>"
							+ "\n3. get <key>"
							+ "\n4. delete <key>"
							+ "\n5. load"
							+ "\n6. stop");				

				}
			}				
		} catch (IOException e) {
			// Print the exception on the console
			e.printStackTrace();
		}

	}
	
	//Fibonacci Hash used as a Compression Map to convert String hashcode to hash array index. 
	static int FiboHash(String key, int size) {
		int index = 0;
		int hsh = Math.abs(key.hashCode());
		double goldenRatio = 0.618033887;		//Knuth proved that conjugate of golden ratio a = (sqrt(5)-1) / 2 proves to be the best suited value to evenly spread the keys
		double temp = hsh * goldenRatio;
		temp = temp % 1;
		temp = temp * size;
		index = (int) Math.floor(temp);
		return index;
	}
	
	//Mod Hash used as the second hash function to calculate the offset
	static int ModHash(String key, int size)
	{
		int index = 0;
		int hsh = key.hashCode();
		index = hsh % size;
		if (index != 0)
		return index;
		else return 1;
	}
	
	static class HashMap{
		int size = 0;
		float load = 0;
		int n =0;
		HashNode array[] = null;
		
		public HashMap(int size) {
			this.size = size;
			array = new HashNode[size];
			for (int i =0; i< size; i++)
			{
				array[i] = new HashNode();
			}
			
		}
		
		public boolean set(String key, Object val)
		{
			if (this.load == 1) return false;
			
			int index = FiboHash(key, this.size);
			int safety = this.size;
			int offset = ModHash(key, this.size);
			
			while(0 != safety--)
			{
				String arrKey = this.array[index].getKeyVal();
				Object arrVal = this.array[index].getRef();
				if (arrKey == null || arrKey.equals(key) || arrVal == null)
				{
					if(arrKey == null || arrVal == null)					//Do not contribute to load if it is just a replacement of value for same key.
						this.load += (float)1/this.size;
					this.array[index].setKeyVal(key);
					this.array[index].setRef(val);
					
					return true;
				}
				index+= offset;
				index = index % this.size;
			}
			return false;
		}
		
		public Object get(String key)
		{
			//Object val =null;
			int index = getHelp(key);
			if(-1 == index)
				return null;
			else
				return this.array[index].getRef();
		}
		
		public int getHelp(String key)
		{
			int val = -1;
			
			int index = FiboHash(key, this.size);
			int safety = this.size;
			int offset = ModHash(key, this.size);
			
			while(0 != safety--)
			{
				String arrKey = this.array[index].getKeyVal();
				Object arrVal = this.array[index].getRef();
				
				if (arrKey.equals(key))
				{
					return index;
				}
				else if (null == arrVal) {
					return -1;
				}
				index+= offset;
				index = index % this.size;
			}
			
			return val;
		}
		
		public Object delete(String key)
		{
			int index = getHelp(key);
			Object val = null;
			if (-1 == index)
			{
				return null;
			}
			else {
				val = this.array[index].getRef();
				this.array[index].setRef(null);
				if(null != val)								//Check to ensure that if double delete is called we dont reduce the load twice
				this.load -= (float)1 /this.size;
				return val;
			}
		}

		public float getLoad() {
			return load;
		}


	}
	
	static class HashNode{
		private Object ref = null;
		private String keyVal = null;
		
		public Object getRef() {
			return ref;
		}
		public void setRef(Object ref) {
			this.ref = ref;
		}
		public String getKeyVal() {
			return keyVal;
		}
		public void setKeyVal(String keyVal) {
			this.keyVal = keyVal;
		}
	}


}
