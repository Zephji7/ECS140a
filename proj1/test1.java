package myTester;

import org.apache.commons.lang3.text.StrBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import java.util.*;

public class test1 {
	
	static HashMap<Color, Integer> cMap = new HashMap<>();
	
	static void colorCount(List<Person> myList){
		int myInt = 2+2;
		int other = 2 -2;
		float myFloat = 2.0 + 2.0;
		float u_not = 2.0000.0000.000;
		String valid = "asdlif0349_";
		String 245tre___ = "";
		int notValid = @#4;
			


		myList.add(null);
        for (Person p : myList){    
        	int colorNum = cMap.getOrDefault(whatColorShoes(p), 0);
        	colorNum++;	
        	cMap.put(whatColorShoes(p), colorNum);
        }
    }

	static class Color{
		
		String color;
		Color(){
			this.color = "undefined";
		}
		Color(String color){
			this.color = color;
		}
	}
	static class Person{
		
		Shoes myShoes;
		
		Person(Shoes myShoes){
			this.myShoes = myShoes;
		}
		
	}
	static class Shoes{
		
		Color color;
		Shoes(Color color){
			this.color  = color;
		}
		Color getColor() {
			return this.color;
		}
	}
	
	static Color whatColorShoes(@NonNull Person nextPerson){

	  //  if (nextPerson == null){
	   //     return new Color("undefined");
	 //   }
	   // else{
	        return nextPerson.myShoes.getColor();
	   // }
	}

	
/*	public static void main(String[] args) {
		
		ArrayList<Person> myList = new ArrayList<>();
		
		if (Math.random()*10 > 5) {
			myList.add(null);
		}
		//myList.add(null);
		colorCount(myList);
		for (Map.Entry<Color, Integer> e: cMap.entrySet()){
			System.out.println(e.getValue());
		}

		//System.out.println("Hello World!");

		//StrBuilder stb = new StrBuilder();

		//@NonNull Object nn = nullable;
		//@NonNull Object nn1 = nullable; // error on this line
		//.out.println(nn);

	}*/
}
