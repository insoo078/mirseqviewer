//package kobic.msb.io.file;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.RandomAccessFile;
//import java.util.LinkedHashMap;
//
//import com.esotericsoftware.kryo.Kryo;
//import com.esotericsoftware.kryo.io.Input;
//import com.esotericsoftware.kryo.io.Output;
//
//import kobic.msb.server.obj.GroupSamInfo;
//import kobic.msb.system.engine.MsbEngine;
//
//public class BamReadObject {
////	public static void writeBamReadObject(String filePath, LinkedHashMap<String, GroupSamInfo> map) throws IOException {
////		File file = new File( filePath );
////		FileOutputStream fout = new FileOutputStream( file );
////	
////		ObjectOutputStream oos = new ObjectOutputStream( fout );
////		oos.writeObject( map );
////		oos.flush();
////		oos.close();
////		fout.close();
////	}
//	private static Kryo kryo = new Kryo();
//	
//	public static String writeBamReadObject(String projectName, String id, LinkedHashMap<String, GroupSamInfo> map) throws IOException {
//		String root_dir = MsbEngine.getInstance().getSystemProperties().getProperty("msb.project.workspace") + File.separator + projectName;
//		String tmp_dir = root_dir + File.separator + "readsMap";
//		
//		File dir = new File( tmp_dir );
//		if( !dir.exists() ) {
//			dir.mkdir();
//		}
//
//		String filename = tmp_dir + File.separator + id + ".reads";
//		
//		File file = new File(filename);
//
//		RandomAccessFile raf = new RandomAccessFile( file, "rw");
//
//		FileOutputStream fout = new FileOutputStream(raf.getFD());
//		Output output = new Output( fout, 4096 );
//		
//		kryo.writeObject(output, map );
//
//		output.flush();
//		output.close();
//		raf.close();
//		fout.close();
//		
//		return filename;
//	}
//	
//	public static LinkedHashMap<String, GroupSamInfo> readBamReadObject(String filepath) throws IOException, ClassNotFoundException {
//		File file = new File(filepath);
//		RandomAccessFile raf = new RandomAccessFile( file, "rw");
//
//		FileInputStream fis = new FileInputStream( raf.getFD() );
////        ObjectInputStream ois = null;
//
////    	ois = new ObjectInputStream( fis );
//    	
//        Input input = new Input( fis, 4096 );
//
//        LinkedHashMap<String, GroupSamInfo> result = (LinkedHashMap<String, GroupSamInfo>)kryo.readObject(input, LinkedHashMap.class);
//
////    	LinkedHashMap<String, GroupSamInfo> result = (LinkedHashMap<String, GroupSamInfo>)ois.readObject();
//
////        ois.close();
//        input.close();
//        raf.close();
//        fis.close();
//        
//        return result;
//	}
//}
