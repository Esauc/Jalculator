import java.util.*;
import javax.swing.JOptionPane;

import javax.script.*;

import java.text.*;

public class Function
{

	private String fx;

	public Function(String function)
	{
		fx = adjust(function);
	}

	/**
	* ALGORITMO getNumber(STRING FUNÇAO, INT POSIÇAO)
	* retorna o numero em uma dada posiçao em uma string ex:
	* funcao = "-52^3+75"
	* getNumber(funcao, 2)
	* Retorna -52
	*/
	public static String getNumber(String s, int pos)
	{

		
		String number = "";

		s = s.replace(" ", ""); //remove os espaços
		s = s.toLowerCase(); //tudo para letra minuscula

		a : if(pos >= 0 && pos < s.length()) //verifica se esta dentro dos limites da string
		{
			char c = s.charAt(pos); // C é o caracter na posição indicada no parâmetro

			// SE C FOR SINAL
			if((c == '+') || (c == '-') || (c == '*') || (c == '/') || (c == '^'))
			{
				number = "";
				int p = 1; //Avança uma posição para compensar o sinal que vai ser adicionado a string

				if(c == '^') //Se for expoente, recomeça o algoritmo na proxima posiçao, quebra A e retorna o valor
				{
					number = getNumber(s, pos+1);
					break a;
				}
				else if(c == '-')
				{
					number = ""+c; //Começa com o sinal do número
				}
				else
				{
					number = "+";
				}

				//percorre a string (->) até encontrar um sinal ou letra
				while(true)
				{
					if(pos+p >= s.length() || pos+p < 0) //Se estiver fora da string, para.
					{
						break;
					}

					char c1 = s.charAt(pos+p); //C1 é o caracter atual que está sendo iterado

					//Se c1 for sinal, para
					if((c1 == '+') || (c1 == '-') || (c1 == '*') || (c1 == '/') || (c1 == '^'))
					{
						break;
					}		
					//se c1 for letra, para
					if(Character.isLetter(c1))
					{
						break;
					}

					number += c1; //adiciona o proximo numero a string
					p ++; //avanca uma posicao
				}
			}

			//SE C FOR NUMERO OU PONTO 
			else if((c == '.') || (Character.isDigit(c)))
			{
				int p = 0; //indice

				//Percorre a string (<-) ate achar um sinal ou acabar a string
				//quando achar um sinal ou string, computa o bloco acima ^
				while(true)
				{

					if(pos-p < 0 || pos-p >= s.length()) // Se estiver fora dos limites da string, para.
					{
						break;
					}

					char c1 = s.charAt(pos-p); //C1 é o caracter atual que está sendo iterado

					// Se c1 for sinal
					if((c1 == '+') || (c1 == '-') || (c1 == '*') || (c1 == '/') || (c1 == '^'))
					{
						number = getNumber(s, pos-p);
						break;
					}

					

					p++;
				}
			}
		}

		number = number.replace("+-", "-");
		return number;
	}

	/**
	* Retorna a função com o formato reconhecido pelo
	* algoritmo. (Remove espaços, sinais desnecessários etc)
	*/
	public static String adjust(String s)
	{
		s = ""+s;
		s = s.replace(" ", "");
		s = s.toLowerCase();
		s = s.replace("x", "*x");
		s = s.replace("-*", "-");
		s = s.replace("+*", "+");
		s = s.replace("**", "*");
		s = s.replace("^*", "^");
		s = s.replace("/*", "/");

		s = s.replace("^", "^+");
		s = s.replace("++", "+");
		s = s.replace("+-", "-");

		s = s.replace("*", "*+");
		s = s.replace("++", "+");
		s = s.replace("+-", "-");

		s = s.replace("/", "/+");
		s = s.replace("++", "+");
		s = s.replace("+-", "-");

		char firstChar = s.charAt(0);

		if(Character.isDigit(firstChar) || Character.isLetter(firstChar))
		{
			s = ("+" + s);
		}
		else if(firstChar == '*')
		{
			s = ("+" + s.substring(1 , s.length()));
		}


		s = s.replace("++", "+");
		s = s.replace("+-", "-");

		return s;
	}

	public float eval(float x)
	{
		String f = fx.replace("x", (""+formatNumber(x))); //substitui o x pelo parametro
		f = adjust(f);
		float n = 0;

		final int PRECISION = 3; //MAX = 3

		
		x = MathUtils.roundDecimalPlaces(x, PRECISION);

		//System.out.println("f(x)= "+f);


		// Resolve as potencias
		for(int i = 0; i < f.length(); i++)
		{
			if(f.charAt(i) == '^')
			{
				n = (float) Math.pow(Float.parseFloat(getNumber(f, i - 1)) , Float.parseFloat(getNumber(f, i+1)));

				if(n != n) //if is NaN
				{
					return n;
				}

				n = MathUtils.roundDecimalPlaces(n, PRECISION);

				//Substitui operaçao pelo resultado, na funçao

				int k = i -(getNumber(f, i-1)).length();

				String s1 = f.substring(0, k);
				String s2 = f.substring(i+(""+getNumber(f, i+1)).length()+1, f.length());

				f = s1 +(n >= 0 ? "+" : "")+ formatNumber(n) + s2;


				f = f.replace("++", "+");
				f = f.replace("+-", "-");

				//volta pro inicio
				i = 0;
			}
		}
		// Resolve as multiplicaçoes *
		for(int i = 0; i < f.length(); i++)
		{
			if(f.charAt(i) == '*')
			{
				n = Float.parseFloat(getNumber(f, i - 1))
				 * Float.parseFloat(getNumber(f, i+1));


				n = MathUtils.roundDecimalPlaces(n, PRECISION);

				//Substitui operaçao pelo resultado, na funçao

				int k = i -(getNumber(f, i-1)).length();

				String s1 = f.substring(0, k);
				String s2 = f.substring(i+(getNumber(f, i+1)).length()+1, f.length());


				f = s1 +(n >= 0 ? "+" : "")+ formatNumber(n) + s2;


				f = f.replace("++", "+");
				f = f.replace("+-", "-");


				//volta pro inicio
				i = 0;
			}
		}
		// Resolve as divisoes /
		for(int i = 0; i < f.length(); i++)
		{
			if(f.charAt(i) == '/')
			{
				n = Float.parseFloat(getNumber(f, i - 1)) / Float.parseFloat(getNumber(f, i+1));


				n = MathUtils.roundDecimalPlaces(n, PRECISION);

				//Substitui operaçao pelo resultado, na funçao

				int k = i -("" +(getNumber(f, i-1))).length();

				String s1 = f.substring(0, k);
				String s2 = f.substring(i+(getNumber(f, i+1)).length()+1, f.length());
				

				f = s1 +(n >= 0 ? "+" : "") + formatNumber(n) + s2;


				f = f.replace("++", "+");
				f = f.replace("+-", "-");


				//volta pro inicio
				i = 0;
			}
		}
		// Resolve as adiçoes +
		for(int i = 1; i < f.length(); i++)
		{
			if(f.charAt(i) == '+')
			{
				n = Float.parseFloat(getNumber(f, i - 1)) + Float.parseFloat(getNumber(f, i+1));


				n = MathUtils.roundDecimalPlaces(n, PRECISION);
				

				int j = 0;
				int k = i -("" +(getNumber(f, i-1))).length();

				String s1 = "";

				if(k > j)
				{
					s1 = f.substring(j, k);
				}

				j = i+(getNumber(f, i+1)).length();
				k = f.length();

				String s2 = "";

				if(j >= 0 && k >= 0 && (k > j))
				{
					 s2 = f.substring(j, k);
				}
				
				f = s1 +(n >= 0 ? "+" : "")+ formatNumber(n) + s2;


				f = f.replace("++", "+");
				f = f.replace("+-", "-");

				//volta pro inicio
				i = 0;
			}
		}
		// Resolve as subtracoes
		for(int i = 1; i < f.length(); i++)
		{
			if(f.charAt(i) == '-')
			{

				n = Float.parseFloat(getNumber(f, i - 1)) + Float.parseFloat(getNumber(f, i+1)); //Soma com o negativo ex: 1+(-2)

				n = MathUtils.roundDecimalPlaces(n, PRECISION);
				
				int j = 0;
				int k = i -(getNumber(f, i-1)).length();

				String s1 = "";

				if(k > j)
				{
					s1 = f.substring(j, k);
				}

				j = i+(getNumber(f, i+1)).length();
				k = f.length();

				String s2 = "";

				if(j >= 0 && k >= 0 && (k > j))
				{
					 s2 = f.substring(j, k);
				}
				
				f = s1 +(n >= 0 ? "+" : "")+ formatNumber(n) + s2;


				f = f.replace("++", "+");
				f = f.replace("+-", "-");

				//volta pro inicio
				i = 0;
			}
		}

		float y = (float) Math.sqrt(-1);

		try
		{
			y = Float.parseFloat(f);
		}
		catch(NumberFormatException ex)
		{
			System.err.println(f);
		}

		return y;
	}


	public float derivative1(float x)
	{
		float h = 0.01f;

		float a = MathUtils.roundDecimalPlaces(eval(x+h), 3);
		float b = MathUtils.roundDecimalPlaces(eval(x-h), 3);

		float dx = (a-b) / (2*h);

		//dx = MathUtils.roundDecimalPlaces(dx, PRECISION);

		return dx;
	}

/*
	public float derivative1(float x)
	{
		final int PRECISION = 3;

		x = MathUtils.roundDecimalPlaces(x, PRECISION);

		float h = (float) (1.0f / (10f *PRECISION));

		float a = eval(x+h);
		float b = eval(x-h);

		float dx = (a-b) / (2*h);

		//dx = MathUtils.roundDecimalPlaces(dx, PRECISION);

		return dx;
	}*/

	//Elimina notaçoes cientificas
	public static String formatNumber(float n)
	{
		DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df.setMaximumFractionDigits(340); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS

		String s = ""+df.format(n);

		return s;
	}


	public String getFunction()
	{
		return fx;
	}
}