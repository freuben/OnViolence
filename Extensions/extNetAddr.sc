+ NetAddr {

	*getHostName {
		var result;
		result = "echo $HOSTNAME".unixCmdGetStdOut;
		^result;
	}

	*ipAddr {arg networkType=\airport;
		var pipe, lines, line, result, type, hostname;

		hostname = this.getHostName;
		if([\host, \local].includes(hostname.asSymbol).not, {

			pipe = Pipe.new(("ping -c 1 " ++ hostname ++ ".local"), "r");
			lines = "";
			line = pipe.getLine;
			while({line.notNil}, {lines = lines ++ line ++ "\n"; line = pipe.getLine; });
			pipe.close;

			if(lines.find("(").notNil, {
				result = lines.copyRange(lines.find("(")+1, lines.find(")")-1);
			}, {"Computer not in network".warn; });

		}, {
			case
			{networkType == \airport} {type = "en1"}
			{networkType == \ethernet} {type = "en0"}
			{networkType == \firewire} {type = "fw0"};
			pipe = Pipe.new(("ifconfig " ++ type ++ " inet"), "r");
			lines = "";
			line = pipe.getLine;
			while({line.notNil}, {lines = lines ++ line ++ "\n"; line = pipe.getLine; });
			pipe.close;

			if(lines.find("inet").notNil, {
				result = lines.copyRange(lines.find("inet")+5, lines.find("netmask")-2);
			}, {
				"No network not found".warn; result="";
			});

		});

		^result;
	}

}