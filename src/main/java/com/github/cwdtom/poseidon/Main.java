package com.github.cwdtom.poseidon;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.github.cwdtom.poseidon.socket.PoseidonSocket;
import org.apache.commons.cli.*;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * 入口类
 *
 * @author chenweidong
 * @since 1.0.0
 */
public class Main {
    /**
     * help
     */
    private static final String HELP = "h";
    /**
     * port
     */
    private static final String PORT = "p";
    /**
     * config file path
     */
    private static final String CONFIG = "c";
    /**
     * version
     */
    private static final String VERSION = "v";

    /**
     * 入口方法
     *
     * @param args 参数
     */
    public static void main(String[] args) throws ParseException, IOException, JoranException {
        Options options = new Options();
        options.addOption(PORT, true, "listen port");
        options.addOption(HELP, false, "show short handbook");
        options.addOption(CONFIG, true, "logback config file path");
        options.addOption(VERSION, false, "show version info");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption(HELP)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ant", options);
        } else if (cmd.hasOption(VERSION)) {
            System.out.println("version: 1.0.0");
        }
        if (cmd.hasOption(CONFIG)) {
            loadConfig(cmd.getOptionValue(CONFIG));
        }
        if (cmd.hasOption(PORT)) {
            new PoseidonSocket(Integer.parseInt(cmd.getOptionValue(PORT)));
        } else {
            // 默认端口10000
            new PoseidonSocket(10000);
        }
    }

    /**
     * 动态导入配置文件
     *
     * @param path 文件路径
     * @throws JoranException 日志配置异常
     * @throws IOException    文件读取异常
     */
    private static void loadConfig(String path) throws JoranException, IOException {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        File externalConfigFile = new File(path);
        if (!externalConfigFile.exists()) {
            throw new IOException("Logback External Config File Parameter does not reference a file that exists");
        } else {
            if (!externalConfigFile.isFile()) {
                throw new IOException("Logback External Config File Parameter exists, but does not reference a file");
            } else {
                if (!externalConfigFile.canRead()) {
                    throw new IOException("Logback External Config File exists and is a file, but cannot be read.");
                } else {
                    JoranConfigurator configurator = new JoranConfigurator();
                    configurator.setContext(lc);
                    lc.reset();
                    configurator.doConfigure(path);
                    StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
                }
            }
        }
    }
}
