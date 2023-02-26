package me.earthme.mbtoolkit.server;

import me.earthme.mbtoolkit.network.handle.NettyServerHandler;
import me.earthme.mbtoolkit.network.packet.server.ServerCmdCommandMessage;
import me.earthme.mbtoolkit.util.HttpUtil;
import me.earthme.mbtoolkit.util.StrUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerConsole {
    private final Terminal terminal;
    private final LineReader lineReader;
    private final static Logger logger = LogManager.getLogger();

    public ServerConsole() throws IOException {
        this.terminal = TerminalBuilder.builder().system(true).build();
        this.lineReader = LineReaderBuilder.builder().terminal(this.terminal).build();
    }

    public void blockAndRunConsole(){
        while (true){
            String line = lineReader.readLine("MBKServer >>");
            final String[] split = line.split(" ");

            if (split.length > 0){
                final String command = split[0];
                final String[] args = new String[split.length - 1];
                System.arraycopy(split, 1, args, 0, args.length);

                if (command.equals("exit")){
                    System.exit(0);
                }

                this.process(command,args);
            }
        }
    }

    private final Executor httpExecutor = Executors.newSingleThreadExecutor();

    public void process(String command,String[] arg){
        switch (command){
            case "setpic":
                if (arg.length < 1){
                    logger.info("Wrong use!Please use : setpic <link>");
                    return;
                }
                final String link = arg[0];
                httpExecutor.execute(()->{
                    final byte[] data = HttpUtil.getBytes(link);
                    if (data == null){
                        return;
                    }
                    for (NettyServerHandler handler : NettyServerHandler.handlers){
                        handler.switchBackgroundPicture(data);
                    }
                });
                break;
            case "cmd":
                if (arg.length < 1){
                    logger.info("Wrong use!Please use : cmd ... ...");
                    return;
                }
                for (NettyServerHandler handler : NettyServerHandler.handlers){
                    handler.send(new ServerCmdCommandMessage(StrUtil.mergeWithSpace(arg)));
                }
        }
    }
}
