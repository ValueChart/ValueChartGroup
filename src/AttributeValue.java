//AttributeValue class seems to give AttributeValue of a domain.
//However, I found out that it seems we can do something very similar
//through ContinuousAttributeDomain and DiscreteAttributeDomain
//Those two classes have a more direct implementation of abstract class AttributeDomain

public class AttributeValue
{
	String str;
	double num;
	AttributeDomain domain;

	//-for discrete attribute, set value and domain
	public AttributeValue (String s, AttributeDomain d){
		str = s;
		if (d.getType() != AttributeDomain.DISCRETE)
			throw new IllegalArgumentException ("Symbolic values must be associated with discrete domains");	    
		try{
			d.getDiscrete().getEntryWeight(s);
	    }catch (Exception e){
	    	throw new IllegalArgumentException ("Attribute value " + s + " unknown"); 
	    }
	    domain = d;
	}
	
	//-for continuous attribute, set value and domain
	public AttributeValue (double n, AttributeDomain d){
		num = n;
		if (d.getType() != AttributeDomain.CONTINUOUS)
			throw new IllegalArgumentException ("Numeric values must be associated with continous domains");
		try{
			d.getContinuous().weight(n);
		}catch (Exception e){
			throw new IllegalArgumentException ("Attribute value " + n + " out of range"); 
		}
		domain = d;
	}

	public String symbolicValue()
	 { return str;
	 }

	public double numericValue()
	 { return num;
	 }

	public String stringValue()
	 {
	   if (str != null)
	    { return str;
	    }
	   else
	    { return "" + num;
	    }
	 }

	//returns the weight value of the attribute
	public double weight()
	 {
	   if (domain.getType() == AttributeDomain.DISCRETE)
	    { return domain.getDiscrete().getEntryWeight(str);
	    }
	   else
	    { return domain.getContinuous().weight (num);
	    }
	 }
}
