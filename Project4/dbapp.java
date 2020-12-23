import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class dbapp {

	static Scanner sc = new Scanner(System.in);
	static Connection con;
	static PreparedStatement pst;
	static Statement st;
	static ResultSet rs;

	public static void main(String[] args) {

		try {

			try {
				Class.forName("org.mariadb.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			try {
				System.out.print("立加 沥焊: ");
				String line = sc.nextLine();
				String tmp[] = line.split(" ");
				con = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:" + tmp[0] + "/music", tmp[1], tmp[2]);

				if (con == null) {
					System.out.println("DB 立加 角菩");
					return;
				}

				System.out.println("DB 立加 己傍");

			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("DB 立加 角菩");
				return;
			}

			st = con.createStatement();
			String act = "1";

			while (act != "0") {

				act = mainMenu();

				switch (act) {
				case "0":
					return;
				case "1":
					try {
						System.out.print("Input Your Manager ID: ");
						int mid = Integer.parseInt(sc.nextLine());
						manager(mid);
						continue;
					} catch (NumberFormatException e) {
						continue;
					}
				case "2":
					try {
						System.out.print("Input Your User ID: ");
						int uid = Integer.parseInt(sc.nextLine());
						user(uid);
						continue;
					} catch (NumberFormatException e) {
						continue;
					}
				case "3":
					search();
					continue;
				case "4":
					newUser();
					continue;
				default:
					continue;
				}
			}

			st.close();
			rs.close();
			con.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String mainMenu() {

		System.out.println("\n------------Main Menu-------------");
		System.out.println("0. Exit");
		System.out.println("1. Manager mode");
		System.out.println("2. User mode");
		System.out.println("3. Search the Music");
		System.out.println("4. I'm New User");
		System.out.println("----------------------------------");
		System.out.print("Input: ");
		String act = sc.nextLine();

		return act;
	}

	public static void manager(int mid) throws SQLException {

		pst = con.prepareStatement("select mid from manager where mid=?");
		pst.setInt(1, mid);
		rs = pst.executeQuery();

		if (!rs.next()) {
			System.out.println("You're not Manager!");
			return;
		}

		while (true) {

			System.out.println("\n-------------Manager--------------");
			System.out.println("ManagerID: " + mid);
			System.out.println("0. Return to previous menu");
			System.out.println("1. Show Registerd Music by Me");
			System.out.println("2. Register new Music");
			System.out.println("3. Remove the Music");
			System.out.println("----------------------------------");
			System.out.print("Input: ");
			String act = sc.nextLine();
			int m;
			boolean flag = true;

			switch (act) {
			case "0":
				return;
			case "1":
				pst = con.prepareStatement("SELECT muid, title FROM music WHERE mid=?");
				pst.setInt(1, mid);
				rs = pst.executeQuery();

				while (rs.next()) {

					if (flag) {
						System.out.println("\n--------------------------------------------");
						System.out.println("    MusicID         Title");
						System.out.println("--------------------------------------------");
						flag = false;
					}

					int muid = rs.getInt(1);
					String title = rs.getString(2);
					System.out.println("   " + muid + "           " + title);

				}

				if (flag) {
					System.out.println("You don't register to Music!");
				}

				continue;
			case "2":
				rs = st.executeQuery("SELECT muid, title FROM music WHERE mid is null");

				while (rs.next()) {

					if (flag) {
						System.out.println("\n--------------------------------------------");
						System.out.println("    MusicID         Title");
						System.out.println("--------------------------------------------");
						flag = false;
					}
					int muid = rs.getInt(1);
					String title = rs.getString(2);
					System.out.println("   " + muid + "           " + title);

				}

				if (flag) {
					System.out.println("All Music are Registerd!");
					continue;
				}

				try {
					System.out.print(">>Input MusicID to Register: ");
					m = Integer.parseInt(sc.nextLine());

					pst = con.prepareStatement("select muid from music where mid is null and muid=?");
					pst.setInt(1, m);
					rs = pst.executeQuery();

					if (!rs.next()) {
						System.out.println("This Music is Already Registered or Not released!");
						continue;
					}

					pst = con.prepareStatement("update music set mid=?,date=now() where muid=?");
					pst.setInt(1, mid);
					pst.setInt(2, m);
					pst.executeQuery();

					continue;
				} catch (NumberFormatException e) {
					continue;
				}
			case "3":
				try {
					System.out.print(">>Input MusicID to Remove: ");
					m = Integer.parseInt(sc.nextLine());

					pst = con.prepareStatement("select mid from music where mid=? and muid=?");
					pst.setInt(1, mid);
					pst.setInt(2, m);
					rs = pst.executeQuery();

					if (!rs.next()) {
						System.out.println("This Music doesn't be registerd by you!");
						continue;
					}

					pst = con.prepareStatement("update music set mid=default,date=default where muid=?");
					pst.setInt(1, m);
					pst.executeQuery();
					
					rs = st.executeQuery("select pname, uid from musiclist where muid="+m);
					
					while(rs.next()) {
						
						String p = rs.getString(1);
						int u = rs.getInt(2);
						pst=con.prepareStatement("update playlist set count=count-1 where pname=? and uid=?");
						pst.setString(1, p);
						pst.setInt(2, u);
						pst.executeQuery();
					}
					
					pst = con.prepareStatement("delete from musiclist where muid=?");
					pst.setInt(1, m);
					pst.executeQuery();

					continue;
				} catch (NumberFormatException e) {
					continue;
				}
			default:
				continue;
			}
		}
	}

	public static void user(int uid) throws SQLException {

		pst = con.prepareStatement("select uid from user where uid in (?)");
		pst.setInt(1, uid);
		rs = pst.executeQuery();

		if (!rs.next()) {
			System.out.println("You're not User!");
			return;
		}

		while (true) {

			System.out.println("\n--------------User--------------");
			System.out.println("UserID: " + uid);
			System.out.println("0. Return to previous menu");
			System.out.println("1. Show my PlayLists");
			System.out.println("2. Create new PlayList");
			System.out.println("3. Remove PlayList");
			System.out.println("4. Add or Remove Music");
			System.out.println("--------------------------------");
			System.out.print("Input: ");
			String act = sc.nextLine();
			String name = null;
			boolean flag = true;

			switch (act) {
			case "0":
				return;
			case "1":
				pst = con.prepareStatement("select pname,count from playlist where uid=?");
				pst.setInt(1, uid);
				rs = pst.executeQuery();

				while (rs.next()) {

					if (flag) {
						System.out.println("\n--------------------------------------------");
						System.out.println("    Playlist Name       The num of Music");
						System.out.println("--------------------------------------------");
						flag = false;
					}

					String pname = rs.getString(1);
					int count = rs.getInt(2);
					System.out.println("     " + pname + "                 " + count);
				}

				if (flag)
					System.out.println("You don't have Playlist!");

				continue;
			case "2":
				System.out.print(">>Input PlayList Name to Create: ");
				name = sc.nextLine();

				try {
					pst = con.prepareStatement("insert into playlist(uid, pname, date) values (?,?,now())");
					pst.setInt(1, uid);
					pst.setString(2, name);
					pst.executeQuery();
				} catch (SQLException e) {
					System.out.println("Already exists this Playlist!");
					continue;
				}

				continue;
			case "3":
				System.out.print(">>Input PlayList Name to Remove: ");
				name = sc.nextLine();

				pst = con.prepareStatement("select pname from playlist where uid=? and pname=?");
				pst.setInt(1, uid);
				pst.setString(2, name);
				rs = pst.executeQuery();

				if (!rs.next()) {
					System.out.println("This Playlist doesn't not already exist!");
					continue;
				}

				pst = con.prepareStatement("delete from playlist where uid=? and pname=?");
				pst.setInt(1, uid);
				pst.setString(2, name);
				pst.executeQuery();

				continue;
			case "4":
				System.out.print(">>Input PlayList Name for this action: ");
				name = sc.nextLine();

				pst = con.prepareStatement("select pname from playlist where uid=? and pname=?");
				pst.setInt(1, uid);
				pst.setString(2, name);
				rs = pst.executeQuery();

				if (!rs.next()) {
					System.out.println("You don't have this Playlist!");
					continue;
				}

				playList(uid, name);
				continue;
			default:
				continue;
			}
		}
	}

	public static void search() throws SQLException {

		while (true) {

			System.out.println("\n-------------Search---------------");
			System.out.println("0. Return to previous menu");
			System.out.println("1. Music title");
			System.out.println("2. Artist");
			System.out.println("3. Agency");
			System.out.println("----------------------------------");
			System.out.print("Input: ");
			String act = sc.nextLine();
			String find;
			String f;
			boolean flag = true;

			switch (act) {
			case "0":
				return;
			case "1":
				System.out.print(">>Music title to Find: ");
				find = sc.nextLine();

				if (find == null)
					continue;

				f = "%" + find + "%";

				pst = con.prepareStatement("SELECT title, name, agency " + "FROM music,artist,released "
						+ "WHERE title like ? AND artist.aid=released.aid AND music.muid=released.muid and mid is not null");
				pst.setString(1, f);
				rs = pst.executeQuery();

				while (rs.next()) {

					if (flag) {
						System.out.println("\n--------------------------------------------");
						System.out.println("    Title      Artist      Agency");
						System.out.println("--------------------------------------------");
						flag = false;
					}

					String t = rs.getString(1);
					String ar = rs.getString(2);
					String ag = rs.getString(3);
					System.out.println("  " + t + "          " + ar + "         " + ag);

				}

				if (flag) {
					System.out.println("Not Found!");
				}

				continue;
			case "2":
				System.out.print(">>Artist to Find: ");
				find = sc.nextLine();

				if (find == null)
					continue;

				f = "%" + find + "%";

				pst = con.prepareStatement("SELECT name, title, agency " + "FROM music,artist,released "
						+ "WHERE name like ? AND artist.aid=released.aid AND music.muid=released.muid and mid is not null");
				pst.setString(1, f);
				rs = pst.executeQuery();

				while (rs.next()) {

					if (flag) {
						System.out.println("\n--------------------------------------------");
						System.out.println("    Artist         Agency        title");
						System.out.println("--------------------------------------------");
						flag = false;
					}

					String ar = rs.getString(1);
					String t = rs.getString(2);
					String ag = rs.getString(3);
					System.out.println("   " + ar + "         " + ag + "       " + t);

				}

				if (flag) {
					System.out.println("Not Found!");
				}

				continue;
			case "3":
				System.out.print(">>Agency to Find: ");
				find = sc.nextLine();

				if (find == null)
					continue;

				f = "%" + find + "%";

				pst = con.prepareStatement("SELECT agency, name, title " + "FROM music,artist,released "
						+ "WHERE agency like ? AND artist.aid=released.aid AND music.muid=released.muid and mid is not null");
				pst.setString(1, f);
				rs = pst.executeQuery();

				while (rs.next()) {

					if (flag) {
						System.out.println("\n--------------------------------------------");
						System.out.println("    Agency       Name       Title");
						System.out.println("--------------------------------------------");
						flag = false;
					}

					String ag = rs.getString(1);
					String ar = rs.getString(2);
					String t = rs.getString(3);
					System.out.println("       " + ag + "        " + ar + "         " + t);

				}

				if (flag) {
					System.out.println("Not Found!");
				}

				continue;
			default:
				continue;
			}

		}
	}

	public static void newUser() throws SQLException {

		int Nuid = 0;

		try {
			System.out.println("");
			System.out.print(">>Input New User ID: ");
			Nuid = Integer.parseInt(sc.nextLine());

			pst = con.prepareStatement("select uid from user where uid=?");
			pst.setInt(1, Nuid);
			rs = pst.executeQuery();

			if (rs.next()) {
				System.out.println("Already exists this User ID!");
				return;
			}
		} catch (NumberFormatException e) {
			return;
		}

		System.out.print(">>Input your name: ");
		String name = sc.nextLine();
		System.out.print(">>Input your rrn: ");
		String rrn = sc.nextLine();
		System.out.print(">>Input your phone: ");
		String phone = sc.nextLine();

		try {
			pst = con.prepareStatement("insert into user(uid, name, rrn, phone) values (?,?,?,?)");
			pst.setInt(1, Nuid);
			pst.setString(2, name);
			pst.setString(3, rrn);
			pst.setString(4, phone);
			pst.executeQuery();
		} catch (SQLException e) {
			System.out.println("Already exists this User or Invalid Input!");
			return;
		}

		rs = st.executeQuery("select uid, name, rrn from user");

		System.out.println("\n--------------------------------------------");
		System.out.println("    UserID         rrn           Name");
		System.out.println("--------------------------------------------");

		while (rs.next()) {

			int Uid = rs.getInt(1);
			String Uname = rs.getString(2);
			String Urrn = rs.getString(3);
			System.out.println("      " + Uid + "        " + Urrn + "        " + Uname);

		}

		return;
	}

	public static void playList(int uid, String pname) throws SQLException {

		while (true) {

			System.out.println("\n-----------Playlist---------------");
			System.out.println("UserID: " + uid + " Playlist: " + pname);
			System.out.println("0. Return to previous menu");
			System.out.println("1. Show my MusicList");
			System.out.println("2. Add new Music");
			System.out.println("3. Remove the Music");
			System.out.println("----------------------------------");
			System.out.print("Input: ");
			String act = sc.nextLine();
			int m;
			boolean flag = true;

			switch (act) {
			case "0":
				return;
			case "1":
				pst = con.prepareStatement("select music.muid, title from music, musiclist "
						+ "where uid=? and pname=? and music.muid=musiclist.muid");
				pst.setInt(1, uid);
				pst.setString(2, pname);
				rs = pst.executeQuery();

				while (rs.next()) {

					if (flag) {
						System.out.println("\n--------------------------------------------");
						System.out.println("    MusicID         Title");
						System.out.println("--------------------------------------------");
						flag = false;
					}

					int muid = rs.getInt(1);
					String title = rs.getString(2);
					System.out.println("        " + muid + "           " + title);

				}

				if (flag)
					System.out.println("This Playlist dosen't have the Music!");

				continue;
			case "2":

				rs = st.executeQuery("select muid, title from music where mid is not null");

				while (rs.next()) {

					if (flag) {
						System.out.println("\n--------------------------------------------");
						System.out.println("    MusicID         Title");
						System.out.println("--------------------------------------------");
						flag = false;
					}
					int muid = rs.getInt(1);
					String title = rs.getString(2);
					System.out.println("        " + muid + "           " + title);

				}

				if (flag) {
					System.out.println("Nothing Music are Registerd!");
					continue;
				}

				try {
					System.out.print(">>Input MusicID to Add: ");
					m = Integer.parseInt(sc.nextLine());

					try {
						pst = con.prepareStatement("insert into musiclist(uid, pname, muid) values (?,?,?)");
						pst.setInt(1, uid);
						pst.setString(2, pname);
						pst.setInt(3, m);
						pst.executeQuery();
					} catch (SQLException e) {
						System.out.println("This Music Already owned This Playlist or Not Registered!");
						continue;
					}

					pst = con.prepareStatement("update playlist set count=count+1 where uid=? and pname=?");
					pst.setInt(1, uid);
					pst.setString(2, pname);
					pst.executeQuery();

					continue;
				} catch (NumberFormatException e) {
					continue;
				}
			case "3":
				try {
					System.out.print(">>Input MusicID to Remove: ");
					m = Integer.parseInt(sc.nextLine());

					pst = con.prepareStatement("select muid from musiclist where uid=? and pname=? and muid=?");
					pst.setInt(1, uid);
					pst.setString(2, pname);
					pst.setInt(3, m);
					rs = pst.executeQuery();

					if (!rs.next()) {
						System.out.println("You don't have Already this Music!");
						continue;
					}

					pst = con.prepareStatement("delete from musiclist where uid=? and pname=? and muid=?");
					pst.setInt(1, uid);
					pst.setString(2, pname);
					pst.setInt(3, m);
					pst.executeQuery();

					pst = con.prepareStatement("update playlist set count=count-1 where uid=? and pname=?");
					pst.setInt(1, uid);
					pst.setString(2, pname);
					pst.executeQuery();

					continue;
				} catch (NumberFormatException e) {
					continue;
				}
			default:
				continue;
			}

		}
	}
}
