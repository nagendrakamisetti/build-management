/*
 * Telnet.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.protocol;

import java.net.*;
import java.io.*;

/**
 * The Telnet class establishes a TCP/IP socket connection 
 * with a telnet server.  This implementation follows the 
 * Telnet protocol defined by RFC 854 and additional RFC
 * documents relating to individual Telnet options.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class Telnet implements Runnable {
    private static final char CR = '\r';
    private static final char LF = '\n';
    private static final String NEWLINE = "\r\n";

    private static final int CMD_SE         = 240;  // End of subnegotiation parameters
    private static final int CMD_NOP        = 241;  // No operation
    private static final int CMD_DM         = 242;  // Data mark
    private static final int CMD_BREAK      = 243;  // NVT Break character
    private static final int CMD_IP         = 244;  // Interrupt process
    private static final int CMD_AO         = 245;  // Abort output
    private static final int CMD_AYT        = 246;  // Are You There
    private static final int CMD_EC         = 247;  // Erase character
    private static final int CMD_EL         = 248;  // Erase line
    private static final int CMD_GA         = 249;  // Go ahead signal
    private static final int CMD_SB         = 250;  // Begin subnegotiation
    private static final int CMD_WILL       = 251;  // Indicate ability to perform option
    private static final int CMD_WONT       = 252;  // Indicates refusal to perform option
    private static final int CMD_DO         = 253;  // Indicates request to perform option
    private static final int CMD_DONT       = 254;  // Indicates request not to perform option
    private static final int CMD_IAC        = 255;  // Interpret As Command

    private static final int FIRST_CMD = CMD_SE;   // First command character
    private static final int LAST_CMD  = CMD_IAC;  // Last command character

    private static final int OPT_BINARY     =   0;  // RFC 856 - Binary transmission
    private static final int OPT_ECHO       =   1;  // RFC 857 - Echo
    private static final int OPT_RECONNECT  =   2;  // RFC ??? - Reconnection
    private static final int OPT_NOGOAHEAD  =   3;  // RFC 858 - Supress Go Ahead
    private static final int OPT_NAMS       =   4;  // RFC ??? - Approx. Msg. Size Negotiation
    private static final int OPT_STATUS     =   5;  // RFC 859 - Status
    private static final int OPT_MARK       =   6;  // RFC 860 - Timing mark
    private static final int OPT_RCTE       =   7;  // RFC 726 - Remote Control Transmit and Echo
    private static final int OPT_LINEWIDTH  =   8;  // RFC ??? - Output Line Width
    private static final int OPT_PAGESIZE   =   9;  // RFC ??? - Output Page Size
    private static final int OPT_CR         =  10;  // RFC 652 - Output Carriage-Return Disposition
    private static final int OPT_HTABS      =  11;  // RFC 653 - Output Horizontal Tab Stops
    private static final int OPT_HTABD      =  12;  // RFC 654 - Output Horizontal Tab Disposition
    private static final int OPT_FF         =  13;  // RFC 655 - Output Formfeed Disposition
    private static final int OPT_VTABS      =  14;  // RFC 656 - Output Vertical Tab Stops
    private static final int OPT_VTABD      =  15;  // RFC 657 - Output Vertical Tab Disposition
    private static final int OPT_LINEFEED   =  16;  // RFC 657 - Output Linefeed Disposition
    private static final int OPT_XASCII     =  17;  // RFC 698 - Extended ASCII
    private static final int OPT_LOGOUT     =  18;  // RFC 727 - Logout
    private static final int OPT_BYTEMACRO  =  19;  // RFC 735 - Byte Macro
    private static final int OPT_DATAENTRY  =  20;  // RFC 1043 - Data Entry Terminal
    private static final int OPT_SUPDUP     =  21;  // RFC 736 - SUPDUP
    private static final int OPT_SUPDUPOUT  =  22;  // RFC 749 - SUPDUP Output
    private static final int OPT_SENDLOC    =  23;  // RFC 779 - Send Location
    private static final int OPT_TTY        =  24;  // RFC 1091 - Terminal Type
    private static final int OPT_EOR        =  25;  // RFC 885 - End of Record
    private static final int OPT_TACACS     =  26;  // RFC 927 - TACACS User Identification
    private static final int OPT_OUTMARK    =  27;  // RFC 933 - Output Marking
    private static final int OPT_TTYLOC     =  28;  // RFC 946 - Terminal Location Number
    private static final int OPT_3270       =  29;  // RFC 1041 - Telnet 3270 Regime
    private static final int OPT_X3         =  30;  // RFC 1053 - X.3 PAD
    private static final int OPT_NAWS       =  31;  // RFC 1073 - Negotiate About Window Size
    private static final int OPT_TTYSPEED   =  32;  // RFC 1079 - Terminal Speed
    private static final int OPT_REMOTEFLOW =  33;  // RFC 1372 - Remote Flow Control
    private static final int OPT_LINEMODE   =  34;  // RFC 1184 - Line mode
    private static final int OPT_XLOC       =  35;  // RFC 1096 - X Display Location
    private static final int OPT_ENV        =  36;  // RFC 1408 - Environment
    private static final int OPT_AUTH       =  37;  // RFC 1416 - Authentication (see also RFC 2941, 2942, 2943, 2951)
    private static final int OPT_ENCRYPT    =  38;  // RFC 2946 - Encryption
    private static final int OPT_NEWENV     =  39;  // RFC 1572 - New Environment
    private static final int OPT_TN3270E    =  40;  // RFC 2355 - TN3270E
    private static final int OPT_XAUTH      =  41;  // RFC ???? - X Authentication
    private static final int OPT_CHARSET    =  42;  // RFC 2066 - Charset
    private static final int OPT_RSERIAL    =  43;  // RFC ???? - Remote Serial Port
    private static final int OPT_COMPORT    =  44;  // RFC 2217 - COM Port Control
    private static final int OPT_NOECHO     =  45;  // RFC ???? - Suppress Local Echo
    private static final int OPT_TLS        =  46;  // RFC ???? - Telnet Start TLS
    private static final int OPT_KERMIT     =  47;  // RFC 2840 - KERMIT
    private static final int OPT_SENDURL    =  48;  // RFC ???? - Send URL
    private static final int OPT_XFORWARD   =  49;  // RFC ???? - Forward X
    private static final int OPT_EXTENDED   = 255;  // RFC 861 - Extended Options

    private static final int FIRST_OPT = OPT_BINARY;    // First option code
    private static final int LAST_OPT  = OPT_XFORWARD;  // Last option code

    /** Keep track of the options which are enabled on the client */
    private boolean[] clientOptions = new boolean[LAST_OPT - FIRST_OPT + 1];

    /** Thread to manage socket I/O. */
    private Thread thread;

    /** Determines whether the I/O thread is processing input. */
    private boolean running = false;

    /** 
     * Emulator class providing callback methods for handling
     * connection input and output.
     */
    private Emulator emulator;

    /** Socket connection to the telnet server */
    private Socket connection;

    /** Input stream for the socket */
    private BufferedInputStream in;

    /** Output stream for the socket */
    private OutputStream out;

    /** Indicates that the connection is in debugging mode */
    private boolean DEBUG = true;

    /** Contains any input characters which have not yet been processed */
    private StringBuffer received = new StringBuffer();

    /** Status will always be true while performing a blocking IO read. */
    private boolean waiting = false;


    /**
     * Construct a telnet connection.  The Emulator will be used to
     * process the input and output of the telnet connection.
     *
     * @param   emulator    Terminal emulation class
     */
    public Telnet(Emulator emulator) {
        this.emulator = emulator;
    }

    /**
     * Construct a telnet connection.  The Emulator will be used to
     * process the input and output of the telnet connection.
     *
     * @param   emulator    Terminal emulation class
     */
    public Telnet(Emulator emulator, String host, int port) {
        this(emulator);
        connect(host, port);
    }


    /**
     * Establish a socket connection with the remote server.
     *
     * @param   host    Hostname of the remote computer
     * @param   port    Port number to connect to
     */
    public synchronized boolean connect(String host, int port) {
        boolean success = false;
        try {
            // Establish the socket connection
            connection = new Socket(host, port);
            in  = new BufferedInputStream(connection.getInputStream());
            out = connection.getOutputStream();

            // Initialize the client options as false 
            for (int idx = 0; idx < clientOptions.length; idx++) {
                clientOptions[idx] = false;
            }

            // Begin reading the socket input
            thread = new Thread(this, "Telnet input monitor");
            thread.start();
            success = true;
        } catch (UnknownHostException exHost) {
            emulator.showMessage("Unable to locate host: " + host + ", Port: " + port + "\n");
            if (DEBUG) emulator.showException(exHost);
        } catch (IOException exIO) {
            emulator.showMessage("Failure to communicate with host: " + host + ", Port: " + port + "\n");
            if (DEBUG) emulator.showException(exIO);
        } catch (Exception ex) {
            emulator.showMessage("An unknown error occurred while attempting to establish a connection.\n");
            if (DEBUG) emulator.showException(ex);
        }

        return success;
    }

    /**
     * Closes the socket connection with the remote server.
     */
    public synchronized boolean disconnect() {
        boolean success = false;

        try {
            running = false;
            if (connection.isConnected()) connection.close();
            success = true;
        } catch (IOException exIO) {
            emulator.showMessage("Failure to close connection with host.\n");
            if (DEBUG) emulator.showException(exIO);
        }

        return success;
    }

    /**
     * Reads a character from the input stream.
     */
    private synchronized char read() throws IOException {
        waiting = true;
        int  asInt  = in.read();
        char asChar = (char)asInt;
        waiting = false;

        // Handle command character if found
        if (asInt == CMD_IAC) {
            negotiate();
            asChar = read();
        }

        return asChar;
    }

    /**
     * Determines if the IO buffer is empty and the process is
     * blocked waiting for further input.
     *
     * @return  TRUE if the process is waiting for IO
     */
    public boolean isWaiting() {
        return waiting;
    }

    /**
     * Returns the contents of the line buffer which has yet to be
     * processed as a complete line.
     *
     * @return  Unprocessed string
     */
    public String getBuffer() {
        return received.toString();
    }

    /**
     * The thread continually reads input from the socket and parses
     * any control characters before the data is passed to the Emulator
     * to be displayed or handled.
     */
    public void run() {
        // Continually loop while the thread is running
        while(running) {
            try {
                // read the next character
                char ch = read();
                if ((int)ch == -1) {
                    if (DEBUG) emulator.showDebug("No more input available.\n");
                } else {
                    received.append(ch);
                }

                // Send each line of text to the emulator when completed
                if (ch == LF) {
                    emulator.received(received.toString());
                    received.delete(0, received.length());
                }
            } catch (IOException ex) {
                disconnect();
            }
        
        } // End WHILE

    }

    /**
     * Negotiate a telnet command by parsing the input stream and 
     * handling any telnet control characters which are found in the 
     * stream.  The following control characters are recognized as
     * part of the RFC 845 Telnet protocol:
     * <pre>
     *      SE                  240    End of subnegotiation parameters.
     *      NOP                 241    No operation.
     *      Data Mark           242    The data stream portion of a Synch.
     *                                 This should always be accompanied
     *                                 by a TCP Urgent notification.
     *      Break               243    NVT character BRK.
     *      Interrupt Process   244    The function IP.
     *      Abort output        245    The function AO.
     *      Are You There       246    The function AYT.
     *      Erase character     247    The function EC.
     *      Erase Line          248    The function EL.
     *      Go ahead            249    The GA signal.
     *      SB                  250    Indicates that what follows is
     *                                 subnegotiation of the indicated
     *                                 option.
     *      WILL (option code)  251    Indicates the desire to begin
     *                                 performing, or confirmation that
     *                                 you are now performing, the
     *                                 indicated option.
     *      WON'T (option code) 252    Indicates the refusal to perform,
     *                                 or continue performing, the
     *                                 indicated option.
     *      DO (option code)    253    Indicates the request that the
     *                                 other party perform, or
     *                                 confirmation that you are expecting
     *                                 the other party to perform, the
     *                                 indicated option.
     *      DON'T (option code) 254    Indicates the demand that the
     *                                 other party stop performing,
     *                                 or confirmation that you are no
     *                                 longer expecting the other party
     *                                 to perform, the indicated option.
     *      IAC                 255    Data Byte 255.
     * </pre>
     */
    private void negotiate() throws IOException {
        // examine the next available character in the stream
        int cmd = in.read();

        switch(cmd) {
            case CMD_SE:
                if (DEBUG) emulator.showDebug("ERROR: Unmatched subnegotiation end command found.\n");
                break;
            case CMD_NOP:   cmdUnsupported(cmd); break;
            case CMD_DM:    cmdUnsupported(cmd); break;
            case CMD_BREAK: cmdUnsupported(cmd); break;
            case CMD_IP:    cmdUnsupported(cmd); break;
            case CMD_AO:    cmdUnsupported(cmd); break;
            case CMD_AYT:   cmdUnsupported(cmd); break;
            case CMD_EC:    cmdUnsupported(cmd); break;
            case CMD_EL:    cmdUnsupported(cmd); break;
            case CMD_GA:    cmdUnsupported(cmd); break;
            case CMD_SB:    subnegotiate(); break;
            case CMD_WILL:  cmdWill(in.read()); break;
            case CMD_WONT:  cmdWont(in.read()); break;
            case CMD_DO:    cmdDo(in.read()); break;
            case CMD_DONT:  cmdDont(in.read()); break;
            default: 
                cmdUnsupported(cmd);
                break;
        }
    }

    /**
     * Handle any unsupported commands.
     *
     * @param   cmd     command code
     */
    private void cmdUnsupported(int cmd) throws IOException {
        if (DEBUG) emulator.showDebug("Unsupported Telnet command: " + cmd + "\n");
    }

    /**
     * Perform the SB subnegotiation telnet commands.  The SB command
     * corresponds to command code 250.
     */
    private void subnegotiate() throws IOException {
        StringBuffer buffer = new StringBuffer();

        // Read all the information until the end of the subnegotiation is found
        int opt = in.read();
        while (opt != CMD_SE) {
            buffer.append((char)opt);
            opt = in.read();
        }

        // Do something with the subnegotiation content
        if (DEBUG) emulator.showDebug("Do something with the subnegotiation content.\n");

    }

    /**
     * Perform the WILL telnet command.  The WILL command corresponds
     * to command code 251.  Options include:
     * <pre>
     * </pre>
     *
     * @param   opt     option code
     */
    private void cmdWill(int opt) throws IOException {
    }

    /**
     * Perform the WONT telnet command.  The WONT command corresponds
     * to command code 252.  Options include:
     * <pre>
     * </pre>
     *
     * @param   opt     option code
     */
    private void cmdWont(int opt) throws IOException {
    }

    /**
     * Perform the DO telnet command.  The DO command corresponds
     * to command code 253.  Options include:
     * <pre>
     * </pre>
     *
     * @param   opt     option code
     */
    private void cmdDo(int opt) throws IOException {
    }

    /**
     * Perform the DONT telnet command.  The DONT command corresponds
     * to command code 254.  Options include:
     * <pre>
     * </pre>
     *
     * @param   opt     option code
     */
    private void cmdDont(int opt) throws IOException {
    }

      
    /**
     * Transmit a command using the Telnet protocol.  The command
     * will automatically be prefixed by the IAC command identifier.
     * 
     * @param   cmd     Command to be transmitted
     * @param   opt     Command option
     */
    private synchronized void transmitCommand(int cmd, int opt) throws IOException {
        out.write(CMD_IAC);
        out.write(cmd);
        out.write(opt);
        out.flush();
    }

    /**
     * Transmit a command using the Telnet protocol.  The command
     * will automatically be prefixed by the IAC command identifier.
     * 
     * @param   cmd     Command to be transmitted
     */
    private synchronized void transmitCommand(int cmd) throws IOException {
        out.write(CMD_IAC);
        out.write(cmd);
        out.flush();
    }


    /**
     * Transmit a string using the Telnet protocol.
     */
    private synchronized void transmit(String str) throws IOException {
        for (int idx = 0; idx < str.length(); idx++) {
            transmit(str.charAt(idx));
        }
    }

    /**
     * Transmit a single character using the Telnet protocol.
     */
    private synchronized void transmit(char ch) throws IOException {
        // Be sure to send the proper CR/LF format
        if (ch == LF) out.write((int)CR);
        out.write((int)ch);
        out.flush();
    }


}
