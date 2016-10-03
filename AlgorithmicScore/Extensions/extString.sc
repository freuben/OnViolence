+ String {

	cnotemidi {
		var twelves, ones, octaveIndex, midis;

		midis = Dictionary[($c->0),($d->2),($e->4),($f->5),($g->7),($a->9),($b->11)];
		ones = midis.at(this[0].toLower);

		if( (this[1].isDecDigit), {
			octaveIndex = 1;
		},{
			octaveIndex = 2;
			if( (this[1] == $#) || (this[1].toLower == $s) || (this[1] == $+), {
				ones = ones + 1;
			},{
				if( (this[1] == $b) || (this[1].toLower == $f) || (this[1] == $-), {
					ones = ones - 1;
				});
			});
		});
		twelves = (this.copyRange(octaveIndex, this.size).asInteger) * 12;
		^(twelves + ones)+12
	}

	charPix {arg letterSize=12,lower=0.5,upper=0.75;
 		var a;
 		this.do{|item| if(item.isLower, {a = a.add(lower)}, {a = a.add(upper)})}
 		^(a.sum*letterSize)
 	}

}
