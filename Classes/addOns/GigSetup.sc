GigSetup {

	*new {arg string, energy="never", airport="off", specialApps=[\quit, "Dropbox"],
		closeOpenApps=true, appExceptions=["Finder", "SuperCollider"];
		^super.new.init(string, energy, airport, specialApps, closeOpenApps, appExceptions);
	}

	init {arg string, energy, airport, specialApps, closeOpenApps, appExceptions;
		Platform.case(\osx, {

			if(closeOpenApps, {
				GigSetup.quitOpenApplications(appExceptions);
			});

			this.window(string,
				{arg usr, pwd;
					var energyScript, airportScript, appScript;
					var appAction, appList;

					energyScript = energy.energySaver(usr, pwd);
					airportScript = airport.airPort;
					appScript = "";

					appAction = specialApps[0];
					appList = specialApps.copyRange(1, specialApps.size-1);

					1.do{
						appList.do{|item|
							appScript = appScript ++ ("\r" ++
								case
								{appAction == \quit} {
									item.quitApp;
								}
								{appAction == \run} {
									item.runApp;
								};
							)
						};

						(energyScript
							++ "\r" ++ airportScript
							++ appScript
						).appleScript;
					};


			});

		},
		\linux, { "This class doesn't support Linux".postln },
		\windows, { "This class doesn't support Windows".postln }
		);

	}

	*perform {
		this.new("Perform", "never", "off", [\quit, "Dropbox", "Google Notifier"]);
	}


	*finish {
		this.new("Finish", "10", "on", [\run, "Dropbox", "Google Notifier"]);
	}

	*energySaver {arg sleepTime="never";

		^super.new.initEnergy(sleepTime.postln);
	}

	initEnergy {arg sleepTime;
		this.window("Energy Saver", {arg usr, pwd;
			var energyScript;
		energyScript = sleepTime.asString.energySaver(usr, pwd);
			energyScript.postln.appleScript;
		});
	}

	window {arg string="Perform", action={arg usr, pwd; [usr,pwd].postln};
		var win, user, text, field;
		win = Window.new(string, Rect(0,0,300,80).center_(Window.availableBounds.center));
		user = "users".unixCmdGetStdOut;
		text = StaticText(win);
		text.string ="Computer Password:";
		text.align = \center;
		field = TextField(win, Rect(0, 0, 150, 20)).background_(Color.black);
		field.action = {arg field;
			action.value(user.copyRange(0, user.size-2), field.value);
			win.close};
		win.layout = VLayout( text, field);
		win.front;
	}

	*quitOpenApplications {arg exceptions=["Finder", "SuperCollider"];
		^exceptions.quitOpenApplications.appleScript;
	}

	*openApplications {arg appArr=["Google Chrome", "Safari"];
		var script;
		script = "";

		appArr.do {|item|
			script = script ++ "\r" ++ item.activateApp;
		};
		script.appleScript;
	}

	*airport {arg cmd="on", port="en0";
		cmd.asString.airPort(port).appleScript;
	}

	*airportOn {arg port="en0";
		"on".airPort(port).appleScript;
	}

	*airportOff {arg port="en0";
		"off".airPort(port).appleScript;
	}

}

+ String {

	quitApp {var return;
		return = ("tell application \"" ++ this ++ "\"\rquit\rend tell");
		^return;
	}

	runApp {var return;
		return = ("tell application \"" ++ this ++ "\"\rrun\rend tell");
		^return;
	}

	activateApp {var return;
		return = ("tell application \"" ++ this ++ "\" to activate\r");
		^return;
	}

	airPort {arg port="en0";
		var return;
		return = ("do shell script \"networksetup -setairportpower " ++ port ++ " " ++ this ++ "\"");
		^return;
	}

	energySaver {arg us, pw; var return, num;
		if(this == "never", {num = "0"}, {num = this});
		return = ("do shell script \"pmset sleep " ++ num ++ " displaysleep "
			++ num ++ "\" user name \"" ++ us ++ "\" password \"" ++ pw
			++ "\" with administrator privileges");
		^return;
	}

	appleScript { ^this.asAppleScriptCmd.unixCmd; }

	asAppleScriptCmd {
		// converts string to applescript via osescript unix command
		var lines, cmd;
		lines = this.split( $\n );

		cmd = "osascript";

		lines.do({ |line|
			cmd = cmd + "-e" + line.asCompileString;
		});

		^cmd;
	}

	keyStroke { |mod = nil, app = "SuperCollider"|
		^this.asKeyStrokeCmd( mod, app ).unixCmd;
	}

	asKeyStrokeCmd { |mod = nil, app = "SuperCollider"|
		// command, option, shift, can be array
		var script;
		// hit a keystroke using applescript
		/* example:
		"k".keyStroke( "command" ); // recompile
		*/
		script =
		"tell application " ++ app.quote ++
		"\nactivate\nend tell\n" ++
		"tell application \"System Events\"\n" ++
		"tell process " ++ app.quote ++
		"\nset frontmost to true\nend tell\n";

		if ( [ String, Symbol ].includes( mod.class ) )
		{ mod = [ mod ] };

		mod.do({ |oneMod|
			script = script ++ "key down" + oneMod.asString ++ "\n";
		});

		script = script ++
		"keystroke" + this.asString.asCompileString ++ "\n";

		mod !? { mod.reverse.do({ |oneMod|
			script = script ++ "key up" + oneMod.asString ++ "\n";
		}); };

		script = script ++ "end tell";

		^script.asAppleScriptCmd;
	}

}

+ SequenceableCollection {

	quitOpenApplications {
		var appExceptions, appSctriptArr, script;

		appExceptions = this;
		appSctriptArr = appExceptions.cs.replace("[", "{").replace("]", "}");

		script = ("tell application \"System Events\" to set the visible of every process to true

set white_list to " ++ appSctriptArr ++ "

try
tell application \"Finder\"
set process_list to the name of every process whose visible is true
end tell
repeat with i from 1 to (number of items in process_list)
set this_process to item i of the process_list
if this_process is not in white_list then
tell application this_process
quit
end tell
end if
end repeat
end try");
		^script;
	}

}