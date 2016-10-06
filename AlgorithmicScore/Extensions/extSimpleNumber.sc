+ SimpleNumber {

	midinoteclass {
		var midi, notes;
		midi = (this + 0.5).asInteger;
		notes = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11];
		^(notes[midi%12])
	}

	midioctave {arg division=12;
		var midi, notes;
		midi = (this + 0.5).asInteger;
		^((midi.div(division))-(12/division)).round(1).linlin(0,127,0,127);
	}
	
	midicnote {
		var midi, notes;
		midi = (this + 0.5).asInteger;
		notes = ["c ", "c#", "d ", "d#", "e ", "f ", "f#", "g ", "g#", "a ", "a#", "b "];
		^(notes[midi%12] ++ (midi.div(12) - 1));
	}

}