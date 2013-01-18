package edu.uw.cs.lil.tiny.utils.string;


public class MultiSpaceRemoval implements IStringFilter {
	
	public static void main(String[] args) {
		System.out.println(new MultiSpaceRemoval()
				.filter("dada  dodo      boo ty    "));
	}
	
	@Override
	public String filter(String str) {
		return str.replaceAll(" +", " ");
	}
}
