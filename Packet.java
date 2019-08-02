import java.io.*;
public class Packet implements Serializable
{
   int n1;
   int n2;

   public Packet(int _n1, int _n2)
   {
      n1 = _n1;
      n2 = _n2;
   }

   public Packet getPacket()
   {
      return this;
   }
   
   public int getN1()
   {
      return n1;
   }
   public int getN2()
   {
      return n2;
   }
   
}