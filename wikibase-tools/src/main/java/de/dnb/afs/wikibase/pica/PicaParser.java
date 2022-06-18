/*
 * PicaParser.java
 *
 * @package de.ddb.conversion.pica
 *
 * @author kett
 *
 * @date 02.04.2004 11:35:37
 */
package de.dnb.afs.wikibase.pica;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ddb.pica.parser.PicaParserException;
import de.ddb.pica.parser.PicaParserHandler;

/**
 * @author heck
 *
 */
public class PicaParser {
    /**
     * Charset in which to handle record field names and identifiers.
     */
    public static final String CHARSET_NAME_ASCII = "ASCII";
    /**
     * Pica+ <code>end-of-record</code>
     */
    public static final byte TOKEN_END_OF_RECORD = 0x1D;

    /**
     * Pica+ <code>end-of-record</code>
     */
    public static final byte TOKEN_LINEFEED = 0x0A;

    /**
     * Pica+ <code>end-of-field</code>
     */
    public static final byte TOKEN_END_OF_FIELD = 0x1E;

    /**
     * Pica+ <code>start-of-subfield</code>
     */
    public static final byte TOKEN_START_OF_SUBFIELD = 0x1F;

    /**
     * Pica+ <code>separator</code>
     */
    public static final byte TOKEN_SEPARATOR = 0x1F;

    /**
     * Pica+ <code>escape</code>
     */
    public static final byte TOKEN_ESCAPE = 0x1B;

    /**
     * Pica+ <code>slash</code> Precedes occurrence if present in title header
     */
    public static final byte TOKEN_SLASH = 0x2F;

    /**
     * Pica+ <code>space</code> Used to introduce a sequence of subfields.
     */
    public static final byte TOKEN_SPACE = 0x20;

    private static final Log LOGGER = LogFactory.getLog(PicaParser.class);

    private enum STATE {
        ENDOFRECORD, FIELD, FIELDHEADER, SUBFIELDHEADER, SUBFIELD
    }

    private PicaParser.STATE state = PicaParser.STATE.ENDOFRECORD;

    private int position;

    private PicaParserHandler handler;

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    PicaParser() {
        // do nothing
    }

    PicaParser(PicaParserHandler handler) {
        this();
        this.handler = handler;
    }

    /**
     * Resets the internal State of this {@link PicaParser}.
     */
    public void reset() {
        setState(STATE.ENDOFRECORD);
        position = 0;
        buffer.reset();
    }
    
    public void nextRecord() {
    	
    }
    

    public void parse(InputStream in) throws PicaParserException {
        if (handler == null) {
            throw new IllegalStateException("No PicaParserHandler defined.");
        }
        position = 0;
        buffer.reset();
        state = STATE.ENDOFRECORD;
        int b = 0;
        try {
            while ((b = in.read()) != -1) {
                ++position;
                if (!isLeadingWhitespace(b)) {
                    handleStateSignals();
                    handleByteSignals(b);
                }
            }
            if (getState() == STATE.FIELDHEADER) {
                /*
                 * This should be an error state but to stay backwards compatible to the former
                 * implementation EOF is treated like end of record.
                 */
                handler.endRecord();
            } else if (getState() != STATE.ENDOFRECORD) {
                throw new PicaParserException("Unexpected end of record.");
            }
        } catch (final IOException e) {
            throw new PicaParserException("Position: " + position + ". " + e.getMessage());
        }
    }

    public void parse(InputStream in, PicaParserHandler handler) throws PicaParserException {
        this.handler = handler;
        parse(in);
    }

    private void handleStateSignals() throws PicaParserException {
        if (isSubfieldHeader()) {
            startSubfield();
        } else if (isFieldHeader()) {
            startField();
        } else if (getState() == STATE.ENDOFRECORD) {
            startRecord();
        }
    }

    private boolean isLeadingWhitespace(int b) {
        return (Character.isWhitespace(b) && getState() == STATE.ENDOFRECORD);
    }

    private void handleByteSignals(int b) throws PicaParserException {
        if (isStartOfSubfield(b)) {
            startSubfieldHeader();
        } else if (isEndOfField(b)) {
            endField();
        } else if (isEndOfRecord(b)) {
            endRecord();
        } else {
            buffer.write(b);
        }
    }

    private void writeContent() throws PicaParserException {
        if (buffer.size() > 0) {
            if (LOGGER.isDebugEnabled()) {
                try {
                    LOGGER.debug("Writing bytes(ASCII): " + buffer.toString(CHARSET_NAME_ASCII));
                } catch (final UnsupportedEncodingException e) {
                    // do nothing
                }
            }
            handler.bytes(buffer.toByteArray());
            buffer.reset();
        }
    }

    private boolean isEndOfField(int b) {
        return b == TOKEN_END_OF_FIELD;
    }

    private boolean isStartOfSubfield(int b) {
        return b == TOKEN_START_OF_SUBFIELD;
    }

    private boolean isSubfieldHeader() {
        return (buffer.size() == 1 && getState() == STATE.SUBFIELDHEADER);
    }

    private boolean isEndOfRecord(int b) {
        return b == TOKEN_END_OF_RECORD || b == TOKEN_LINEFEED;
    }

    private void startRecord() {
        handler.startRecord();
        setState(STATE.FIELDHEADER);
    }

    private void endRecord() throws PicaParserException {
        handler.endRecord();
        setState(STATE.ENDOFRECORD);
    }

    private boolean isFieldHeader() {
        return (getState() == STATE.FIELDHEADER && buffer.size() == 5 && buffer.toByteArray()[4] == ' ')
                || (getState() == STATE.FIELDHEADER && buffer.size() == 8);
    }

    private void startField() throws PicaParserException {
        String header;
        try {
            header = buffer.toString(CHARSET_NAME_ASCII);
            buffer.reset();

            int occurence = 0;
            String fieldName = header.trim();
            if (header.indexOf(TOKEN_SLASH) > -1) {
                final String[] parts = header.split(String.valueOf((char) TOKEN_SLASH));
                fieldName = parts[0].trim();
                occurence = Integer.parseInt(parts[1].trim());
            }
            if (fieldName == null || fieldName.length() == 0) {
                throw new IllegalStateException("Fieldname must not be null or empty at position " + position + ".");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Start field {fieldName=" + fieldName + ", occurence=" + occurence + "}.");
            }
            handler.startField(fieldName, occurence);
            setState(STATE.FIELD);
        } catch (final UnsupportedEncodingException e) {
            throw new PicaParserException("Position: " + position + ". " + e.getMessage());
        }
    }

    private void endField() throws PicaParserException {
        if (getState() == STATE.SUBFIELDHEADER) {
            throw new PicaParserException("Illegal state [" + getState() + "] on end of field.");
        } else if (getState() == STATE.SUBFIELD) {
            endSubfield();
        }
        writeContent();
        handler.endField();
        setState(STATE.FIELDHEADER);
    }

    private void startSubfieldHeader() throws PicaParserException {
        if (getState() == STATE.SUBFIELD) {
            endSubfield();
        }
        setState(STATE.SUBFIELDHEADER);
    }

    private void startSubfield() throws PicaParserException {
        if (getState() != STATE.SUBFIELDHEADER) {
            throw new PicaParserException("Illegal state [" + getState() + "]. SUBFIELDHEADER expected.");
        }
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Start subfield {code=" + buffer.toString(CHARSET_NAME_ASCII) + "}.");
            }
            handler.startSubfield(buffer.toString(CHARSET_NAME_ASCII));
            buffer.reset();
            setState(STATE.SUBFIELD);
        } catch (final UnsupportedEncodingException e) {
            throw new PicaParserException(e);
        }
    }

    private void endSubfield() throws PicaParserException {
        writeContent();
        handler.endSubfield();
    }

    private void setState(PicaParser.STATE state) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Set state [" + state + "]. Position [" + position + "].");
        }
        this.state = state;
    }

    private STATE getState() {
        return state;
    }

}
