import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class bptree {

	static class node {

		static class pair {

			int key; // key
			int val; // value
			node child; // child of pair

			pair(int key, int val, node child) {

				this.key = key;
				this.val = val;
				this.child = child; // 왼쪽 자식

			}

		}

		int m; // pair의 개수
		List<pair> p; // <key, left_child_node> or <key, data>
		node r; // 가장 오른쪽 자식 or 오른쪽 형제 노드
		node parent; // 부모 노드

		node() {

			this.m = 0;
			this.p = new ArrayList<>();
			this.r = null;
			this.parent = null;

		}

	}

	public static int M = 0; // 차수
	public static int count = 0; // 노드 번호 세기

	public static node root = new node();

	public static List<node> tree = new ArrayList<>();
	public static List<Integer> leaf = new ArrayList<>();

	public static void main(String[] args) {

		String act = args[0];
		String index_file = args[1];
		List<Integer> data = new ArrayList<>();

		switch (act) {

		case "-c": // create

			M = Integer.parseInt(args[2]); // 차수 저장

			create(index_file);
			break;

		case "-i": // insert

			String in_data = args[2];

			read_tree(index_file);
			read_data(in_data, act);
			break;

		case "-d": // delete

			String de_data = args[2];

			read_tree(index_file);
			read_data(de_data, act);
			break;

		case "-s": // single key search

			int find_key = Integer.parseInt(args[2]);

			read_tree(index_file);
			single_key_find(find_key);
			break;

		case "-r": // ranged search

			int from_key = Integer.parseInt(args[2]);
			int to_key = Integer.parseInt(args[3]);

			read_tree(index_file);
			ranged_search(from_key, to_key);
			break;

		}

		if (act.equals("-i") || act.equals("-d"))
			save_layout(index_file);

	}

	public static void save_layout(String index_file) { // 완료

		try {

			FileWriter w = new FileWriter(index_file, false);

			w.write(M + "\n");
			save(w, root, 0);

			w.write("@");
			for (int i = 0; i < leaf.size(); i++)
				w.write(" " + leaf.get(i));

			w.flush();
			w.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void create(String index_file) {

		try {

			FileWriter w = new FileWriter(index_file, false);
			w.write(Integer.toString(M));
			w.flush();
			w.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void single_key_find(int find_key) {

		List<node> path = new ArrayList<>();
		node now = root;
		int find = -1;

		while (true) {

			boolean flag = false;

			if (now.m == 0 || now.p.get(0).child == null) {
				path.add(now);
				break;
			}

			for (int i = 0; i < now.m; i++) {

				if (find_key < now.p.get(i).key) {

					path.add(now);
					now = now.p.get(i).child;

					flag = true;
					break;

				}

			}

			if (!flag) {

				path.add(now);
				now = now.r;

			}

		}

		for (int i = 0; i < now.m; i++) {

			if (now.p.get(i).key == find_key) {

				find = i;
				break;
			}
		}

		if (find == -1)
			System.out.print("NOT FOUND");

		else {

			for (int i = 0; i < path.size() - 1; i++) {

				for (int j = 0; j < path.get(i).m; j++) {

					if (j == 0)
						System.out.print(path.get(i).p.get(j).key);
					else
						System.out.print("," + path.get(i).p.get(j).key);
				}
				System.out.print("\n");
			}
			System.out.println(now.p.get(find).val);

		}

	}

	public static void ranged_search(int from_key, int to_key) {

		node now = leaf_find(from_key);
		List<node.pair> path = new ArrayList<>();
		boolean flag = false;

		while (true) {

			if (now == null)
				break;

			for (int i = 0; i < now.m; i++) {

				if (now.p.get(i).key > to_key) {
					flag = true;
					break;
				}

				if (from_key <= now.p.get(i).key)
					path.add(now.p.get(i));

			}

			if (flag)
				break;

			now = now.r;

		}

		for (int i = 0; i < path.size(); i++)
			System.out.println(path.get(i).key + ", " + path.get(i).val);

	}

	public static node leaf_find(int find_key) {

		node now = root;

		while (true) {
			boolean flag = false;

			if (now.m == 0 || now.p.get(0).child == null)
				return now;

			for (int i = 0; i < now.m; i++) {

				if (find_key < now.p.get(i).key) {
					now = now.p.get(i).child;
					flag = true;
					break;
				}
			}

			if (!flag) {
				now = now.r;
			}
		}

	}

	public static node leaf_find_left(int find_key) {

		node now = root;

		while (true) {
			boolean flag = false;

			if (now.m == 0 || now.p.get(0).child == null)
				return now;

			for (int i = 0; i < now.m; i++) {

				if (find_key <= now.p.get(i).key) {
					now = now.p.get(i).child;
					flag = true;
					break;
				}
			}

			if (!flag) {
				now = now.r;
			}
		}

	}

	public static int index_find(node now, int find_key) {

		for (int i = 0; i < now.m; i++) {

			if (find_key < now.p.get(i).key)
				return i;

		}

		return now.m;

	}

	public static void leaf_split(node now, node right) {

		for (int i = M / 2; i < M; i++) {

			right.p.add(new node.pair(now.p.get(i).key, now.p.get(i).val, null));
			right.m++;

		}
		for (int i = M / 2; i < M; i++) {

			now.p.remove(M / 2);
			now.m--;

		}

	}

	public static void insert(int key, int val) { // leaf node insert

		node now = leaf_find(key);
		node.pair newN = new node.pair(key, val, null);

		now.p.add(index_find(now, key), newN);
		now.m++;

		if (now.m < M)
			return;

		node right = new node();
		leaf_split(now, right);

		if (now == root) {

			node parent = new node();
			parent.p.add(new node.pair(right.p.get(0).key, right.p.get(0).val, now));
			parent.r = right;
			parent.m++;

			root = parent;

			now.parent = parent;
			right.parent = parent;

			right.r = now.r;
			now.r = right;

		} else {

			if (index_find(now.parent, key) == now.parent.m) { // 맨 오른쪽

				now.parent.p.add(new node.pair(right.p.get(0).key, right.p.get(0).val, now));
				now.parent.r = right;
				now.parent.m++;

			} else {

				int idx = index_find(now.parent, key);
				now.parent.p.add(idx, new node.pair(right.p.get(0).key, right.p.get(0).val, now));
				now.parent.p.get(idx + 1).child = right;
				now.parent.m++;

			}

			right.r = now.r;
			now.r = right;

			right.parent = now.parent;

			if (now.parent.m >= M) {
				parent_split(now.parent);
			}

			return;
		}

	}

	public static void save(FileWriter w, node now, int parent) {

		count++;
		int n = count; // 지금 노드의 인덱스

		try {

			w.write("$ " + parent + " " + n + " / ");

			for (int i = 0; i < now.m; i++)
				w.write(now.p.get(i).key + " " + now.p.get(i).val + " / ");

			w.write("\n");

			for (int i = 0; i < now.m; i++) {

				if (now.p.get(i).child != null)
					save(w, now.p.get(i).child, n);

			}

			if (now.p.get(0).child != null && now.r != null) // non-leaf
				save(w, now.r, n);

			if (now.p.get(0).child == null) // leaf
				leaf.add(n);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void read_data(String fileName, String act) {

		List<Integer> arr = new ArrayList<>();
		File readfile = new File(fileName);

		String line = null;

		try {

			BufferedReader reader = new BufferedReader(new FileReader(readfile));

			while ((line = reader.readLine()) != null) {

				if (act.equals("-i")) {

					String[] tmp = line.split(",");
					int key = Integer.parseInt(tmp[0]);
					int val = Integer.parseInt(tmp[1]);

					insert(key, val);

				} else
					delete(Integer.parseInt(line));

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void read_tree(String fileName) {

		File readfile = new File(fileName);
		tree.add(0, new node());

		String line = null;
		boolean flag = true;

		try {

			BufferedReader reader = new BufferedReader(new FileReader(readfile));

			while ((line = reader.readLine()) != null) {

				if (flag) {

					M = Integer.parseInt(line);
					flag = false;

				} else {

					tree.add(null); // node num starts from 1.

					if (line.substring(0, 1).equals("$")) {

						String[] data = line.substring(2).split(" / ");

						int parent_index = Integer.parseInt(data[0].split(" ")[0]);
						int my_index = Integer.parseInt(data[0].split(" ")[1]);

						node now = new node();

						// add pairs to node
						for (int i = 1; i < data.length; i++) {

							int key = Integer.parseInt(data[i].split(" ")[0]);
							int value = Integer.parseInt(data[i].split(" ")[1]);
							now.p.add(new node.pair(key, value, null));
							now.m++;

						}

						// connect parent
						if (parent_index == 0) {

							tree.add(my_index, now);
							root = now;
							root.parent = null;

						} else {

							tree.add(my_index, now);
							now.parent = tree.get(parent_index);

							boolean connect = false;
							for (int i = 0; i < now.parent.m; i++) {

								if (now.parent.p.get(i).child == null) {

									now.parent.p.get(i).child = now;
									connect = true;
									break;

								}

							}

							if (!connect)
								now.parent.r = now;

						}

					} else { // leaf

						String[] tmp = line.split(" ");

						int idx = 1;

						for (int i = 1; i < tmp.length - 1; i++) {

							tree.get(Integer.parseInt(tmp[idx])).r = tree.get(Integer.parseInt(tmp[idx + 1]));
							idx++;

						}

						break;
					}
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int index_find_parent(node now) {

		if (now.parent != null) {

			for (int i = 0; i < now.parent.m; i++) {

				if (now.parent.p.get(i).child == now)
					return i;

			}

			if (now.parent.r == now) // 맨 오른쪽 자식
				return now.parent.m;

		}

		return -1; // 존재하지 않음

	}

	public static node find_sibling(node now, String act) {

		if (now.parent == null)
			return null;

		int idx = index_find_parent(now);

		if (idx == 0) { // now가 맨 왼쪽 자식

			if (act.equals("left"))
				return null;

			else { // right

				if (now.parent.m == 1)
					return now.parent.r;
				else
					return now.parent.p.get(idx + 1).child;

			}

		} else {

			if (act.equals("left"))
				return now.parent.p.get(idx - 1).child;

			else { // right

				if (now.parent.m == idx) // now가 맨 오른쪽 자식
					return null;
				else if (now.parent.m == idx + 1)
					return now.parent.r;
				else
					return now.parent.p.get(idx + 1).child;

			}

		}

	}

	public static void swap_key(int delete_key, int swap_key) {

		node now = root;

		while (true) {

			boolean flag = false;

			if (now.m == 0 || now.p.get(0).child == null)
				break;

			for (int i = 0; i < now.m; i++) {

				if (delete_key <= now.p.get(i).key) {

					if (delete_key == now.p.get(i).key) {

						now.p.get(i).key = swap_key;
						return;

					}

					now = now.p.get(i).child;
					flag = true;
					break;
				}
			}

			if (!flag)
				now = now.r;

		}
	}

	public static void parent_split(node now) {

		int size = now.m;

		node right = new node();
		node.pair tmp = new node.pair(now.p.get(size / 2).key, now.p.get(size / 2).val, now.p.get(size / 2).child);

		for (int i = size / 2 + 1; i < size; i++) {

			right.p.add(new node.pair(now.p.get(i).key, now.p.get(i).val, now.p.get(i).child));
			right.m++;
			now.p.get(i).child.parent = right;

		}
		for (int i = size / 2; i < size; i++) {

			now.p.remove(size / 2);
			now.m--;

		}

		right.r = now.r;
		now.r = tmp.child;

		right.r.parent = right;
		now.r.parent = now;

		if (now.parent == null) { // root

			node parent = new node();
			parent.p.add(new node.pair(tmp.key, tmp.val, now));
			parent.r = right;
			parent.m++;

			root = parent;

			now.parent = parent;
			right.parent = parent;

		} else {

			int idx = index_find_parent(now);

			now.parent.p.add(idx, new node.pair(tmp.key, tmp.val, now));
			now.parent.m++;

			if (idx == now.parent.m - 1) // 맨 오른쪽
				now.parent.r = right;
			else
				now.parent.p.get(idx + 1).child = right;

			right.parent = now.parent;

			if (now.parent.m >= M)
				parent_split(now.parent);

		}

	}

	public static void merge_parent(node now) {

		int idx = index_find_parent(now);

		if (now.parent != null) {

			node left = find_sibling(now, "left");
			node right = find_sibling(now, "right");

			if (left != null) {

				// left를 자식으로 갖는 pair 저장
				int key = now.parent.p.get(idx - 1).key;
				int val = now.parent.p.get(idx - 1).val;		

				now.parent.p.remove(idx - 1);
				now.parent.m--;

				left.p.add(new node.pair(key, val, left.r));
				left.m++;

				for (int i = 0; i < now.m; i++) {

					left.p.add(now.p.get(i));
					left.m++;
					now.p.get(i).child.parent = left;

				}

				left.r = now.r;
				left.r.parent = left;

				if (now.parent.m > 0 && idx - 1 != now.parent.m) {

					node.pair tmp = now.parent.p.get(idx - 1);
					now.parent.p.set(idx - 1, new node.pair(tmp.key, tmp.val, left));

				} else
					now.parent.r = left;

				if (left.m >= M)
					parent_split(left);

				if (now.parent.m == 0 && now.parent == root) {
				
					root = left;
					root.parent = null;
				
				} else if (now.parent != root && now.parent.m < (M - 1) / 2)
					merge_parent(now.parent);

			} else if (right != null) {

				// now를 자식으로 갖는 pair 저장
				int key = now.parent.p.get(idx).key;
				int val = now.parent.p.get(idx).val;
				
				now.parent.p.remove(idx);
				now.parent.m--;
				
				now.p.add(new node.pair(key, val, now.r));
				now.m++;

				for (int i = 0; i < right.m; i++) {

					now.p.add(right.p.get(i));
					now.m++;
					right.p.get(i).child.parent = now;

				}
				
				now.r = right.r;
				now.r.parent = now;

				if (right.parent.m > 0 && idx!= right.parent.m) {

					node.pair tmp = right.parent.p.get(idx);
					right.parent.p.set(idx, new node.pair(tmp.key, tmp.val, now));

				} else
					right.parent.r = now;

				if (now.m >= M)
					parent_split(now);

				if (right.parent.m == 0 && right.parent == root) {
				
					root = now;
					root.parent = null;
				
				} else if (right.parent != root && right.parent.m < (M - 1) / 2)
					merge_parent(right.parent);
				
			}

		}

	}

	public static void delete(int key) { // leaf delete

		node now = root;
		int idx;

		while (true) {

			idx = now.m;

			for (int i = 0; i < now.m; i++) {

				if (now.p.get(i).key > key) {

					idx = i;
					break;

				}

			}

			if (now.m == 0 || now.p.get(0).child == null)
				break;

			if (idx == now.m)
				now = now.r;
			else
				now = now.p.get(idx).child;

		}

		idx = -1;
		for (int i = 0; i < now.m; i++) {

			if (key == now.p.get(i).key)
				idx = i;

		}

		if (idx == -1) {
			System.out.println("No delete " + key);
			return;
		}

		node left_leaf = leaf_find_left(now.p.get(0).key);

		int tmp_key = now.p.get(0).key;

		now.p.remove(idx);
		now.m--;

		if (now.m >= (M - 1) / 2) {
			swap_key(tmp_key, now.p.get(0).key);
			return;
		}

		node left = find_sibling(now, "left");
		node right = find_sibling(now, "right");

		if (left != null && left.m > (M - 1) / 2) {

			now.p.add(0, new node.pair(left.p.get(left.m - 1).key, left.p.get(left.m - 1).val, null));
			now.m++;

			left.p.remove(left.m - 1);
			left.m--;

			swap_key(tmp_key, now.p.get(0).key);

		} else if (right != null && right.m > (M - 1) / 2) {

			now.p.add(new node.pair(right.p.get(0).key, right.p.get(0).val, null));
			now.m++;

			right.p.remove(0);
			right.m--;

			swap_key(now.p.get(now.m - 1).key, right.p.get(0).key);

			if (idx == 0)
				swap_key(tmp_key, now.p.get(0).key);

		} else {

			if (now.parent != null) {

				int index = index_find_parent(now);

				if (left != null) {

					for (int i = 0; i < now.m; i++) {

						left.p.add(new node.pair(now.p.get(i).key, now.p.get(i).val, null));
						left.m++;

					}

					left.r = now.r;

					if (index == now.parent.m)
						now.parent.r = left;
					else
						now.parent.p.get(index).child = left;

					now.parent.p.remove(index - 1);
					now.parent.m--;

					if (now.parent == root) {

						if (now.parent.m > 0)
							return;
						else {
							root = left;
							root.parent = null;
						}

					}

					else {

						if (now.parent.m >= (M - 1) / 2)
							return;

						else
							merge_parent(now.parent);
					}

				} else {

					int tmp = right.p.get(0).key;

					for (int i = now.m - 1; i >= 0; i--) {

						right.p.add(0, new node.pair(now.p.get(i).key, now.p.get(i).val, null));
						right.m++;

					}

					if (idx == 0)
						swap_key(key, right.p.get(0).key);

					if (left_leaf != null)
						left_leaf.r = right;

					now.parent.p.remove(index);
					now.parent.m--;

					if (now.parent == root) {

						if (now.parent.m > 0)
							return;
						else {
							root = right;
							root.parent = null;
						}

					}

					else {

						if (now.parent.m >= (M - 1) / 2)
							return;
						else
							merge_parent(now.parent);

					}

				}

			}
		}
	}

}
