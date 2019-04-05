//The decrypted message is "The Magic Words are Squeamish Ossifrage"

package attackingclient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class AttackingClient {

    public static void main(String[] args) {
        
        String host = "http://crypto-class.appspot.com/po?er=";
          String cipherText = "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4";
        //String cipherText = "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4";
                                                                                               
        //Blocks ciphers
        //0 f20bdba6ff29eed7b046d1df9fb70000  --> IV - not encrypted
        //1 58b1ffb4210a580f748b4ac714c001bd
        //2 4a61044426fb515dad3f21f18aa577c0
        //3 bdf302936266926ff37dbf7035d5eeb4    
        
        char[] plainText = new char[48];
        
        //System.out.println(getRequest(server, "f20bdba6ff29eed7b046d1df9fb7002158b1ffb4210a580f748b4ac714c001bd"));
        
        crackBlock(host, "f20bdba6ff29eed7b046d1df9fb70000", "58b1ffb4210a580f748b4ac714c001bd", plainText, 1);
        System.out.println(Arrays.toString(plainText));
        
        crackBlock(host, "58b1ffb4210a580f748b4ac714c001bd", "4a61044426fb515dad3f21f18aa577c0", plainText, 2);
        System.out.println(Arrays.toString(plainText));
        
        crackBlock(host, "4a61044426fb515dad3f21f18aa577c0", "bdf302936266926ff37dbf7035d5eeb4", plainText, 3);
        System.out.println(Arrays.toString(plainText));
        
        String phrase = "";
        for(char i :plainText){
            phrase += i;
        }
        System.out.println(phrase);
        
    }
    
    static void crackBlock(String host, String block1, String block2, char[] plainText, int blockNum) {
        String block = block1 + block2;
        int statusCode;
        int plainTextChar;
        boolean bool = false;
        for(int j=15; j>=0; j--) {
            
            for(int i=0; i<256; i++) {
                //allazei to byte 256 fores ews na ferei 404
                if(i<16) 
                    block = block.substring(0, (j+1)*2-2) + "0" + Integer.toHexString(i) + block.substring((j+1)*2, 64);  
                else 
                    block = block.substring(0, (j+1)*2-2) + Integer.toHexString(i) + block.substring((j+1)*2, 64);
                
                
                statusCode = getRequest(host, block); 
                
                System.out.println(i + " -- " + statusCode);
                //if(i == 192 && statusCode == 404)
                //    continue;
                
                //if i.equals(original byte)
                if( statusCode == 404 && bool == false && (Integer.toHexString(i).equals( block1.substring(j*2, j*2+2) ) || ("0" + Integer.toHexString(i)).equals(block1.substring(j*2, j*2+2)) ) ) {
                    bool = true;
                    continue;
                }
                
                //if bool==true and no other byte than the original was found to return 404
                if( bool == true && i == 255 && statusCode != 404){
                    i = Integer.parseInt(block1.substring(j*2, j*2+2), 16) - 1;
                }
                
                if( statusCode == 404 /*&& !(Integer.toHexString(i).equals( block1.substring(j*2, j*2+2) ) || ("0" + Integer.toHexString(i)).equals(block1.substring(j*2, j*2+2)) )*/ ) {                    
                    System.out.println(i + " -- 404");
                    bool = false;
                    
                    //PLAINTEXT
                    plainText[(blockNum-1)*16+j] = (char) (Integer.parseInt(block1.substring(j*2, j*2+2), 16) ^ i ^ (16-j));
                    
                    for(int a=j+1; a<=16; a++)            
                        if( ((16-j+1) ^ Integer.parseInt(block1.substring(a*2-2, a*2), 16) ^ plainText[(blockNum-1)*16+a-1] ) < 16)
                            block = block.substring(0, a*2-2) + "0" + Integer.toHexString( (16-j+1) ^ Integer.parseInt(block1.substring(a*2-2, a*2), 16) ^ plainText[(blockNum-1)*16+a-1] ) + block.substring(a*2, 64);
                        else
                            block = block.substring(0, a*2-2) + Integer.toHexString( (16-j+1) ^ Integer.parseInt(block1.substring(a*2-2, a*2),16) ^ plainText[(blockNum-1)*16+a-1] ) + block.substring(a*2, 64);

                    break;
                }      //if statusCode == 404 
                
            }   //i loop 00 to ff
            
            System.out.println(Arrays.toString(plainText));
        
        }   //byte loop 15 to 0
        
    }   //function
    
    static int getRequest(String host, String cipherText) {
        int statusCode = 0;
        try{
            URL url = new URL(host + cipherText);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            statusCode = http.getResponseCode();
        }catch(IOException e){
            System.out.println("Connection failed!!");
        }
        return statusCode;
    }
    
}
