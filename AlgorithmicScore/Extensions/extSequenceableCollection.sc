+ SequenceableCollection {

asAscii {  ^String.fill(this.size,
		{|i| {this[i].asInteger.asAscii}.try ? "" }
	)} // part of wslib

}