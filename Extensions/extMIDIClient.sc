+ MIDIClient {
	
* getUid {arg func={arg uid; uid.postln};
	var labelArr, window, menu;

sources = this.sources;

sources.do{|item|
	labelArr = labelArr.add(item.device ++ " " ++ item.name);
};

window = Window.new("MIDI devices", Rect(0,0,300,50).center_(Window.availableBounds.center));
menu = PopUpMenu(window,Rect(10,10,280,20));
menu.items =  labelArr;
menu.action = {arg menu;
func.value(sources[menu.value].uid);
window.close;	
};
menu.allowsReselection = true;
window.front;
}	
	
}