+ String {
	
//	airPort {var return;
//		return = ("do shell script \"networksetup -setairportpower en0 " ++ this ++ "\"");
//		^return;	
//	}
//	
//	energySaver {arg us, pw; var return, num;
//		if(this == "never", {num = "0"}, {num = this});
//		return = ("do shell script \"pmset sleep " ++ num ++ " displaysleep " ++ num ++ "\" user name \"" ++ us ++ "\" password \"" ++ pw ++ "\" with administrator privileges");
//		^return;
//	}
	
	script {
		var win, user, text, field;
	win = Window.new(this, Rect(0,0,300,80).center_( Window.availableBounds.center ));
	user = "users".unixCmdGetStdOut;
	text = StaticText(win);
	text.string ="Computer Password:";
	text.align = \center;
	field = TextField(win, Rect(0, 0, 150, 20)).background_(Color.black);
	field.action = {arg field; "RUN SCRIPT with ".postln; user.copyRange(0, user.size-2).postln; 	field.value.postln; win.close};
	win.layout = VLayout( text, field);
	win.front;
	}
	
}