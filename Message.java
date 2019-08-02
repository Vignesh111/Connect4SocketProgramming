import java.io.*;
public class Message implements Serializable
{
   String message;
   public Message(String _message)
   {
      message = _message;
   }
   
   public String getM()
   {
      return message;
   }


}