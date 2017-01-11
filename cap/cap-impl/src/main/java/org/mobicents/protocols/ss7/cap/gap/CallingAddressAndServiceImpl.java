/*
 * TeleStax, Open Source Cloud Communications  Copyright 2012.
 * and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.protocols.ss7.cap.gap;

import java.io.IOException;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;
import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.mobicents.protocols.ss7.cap.api.CAPException;
import org.mobicents.protocols.ss7.cap.api.CAPParsingComponentException;
import org.mobicents.protocols.ss7.cap.api.CAPParsingComponentExceptionReason;
import org.mobicents.protocols.ss7.cap.api.gap.CallingAddressAndService;
import org.mobicents.protocols.ss7.cap.api.isup.Digits;
import org.mobicents.protocols.ss7.cap.isup.DigitsImpl;
import org.mobicents.protocols.ss7.cap.primitives.CAPAsnPrimitive;

/**
 *
 * @author <a href="mailto:bartosz.krok@pro-ids.com"> Bartosz Krok (ProIDS sp. z o.o.)</a>
 */
public class CallingAddressAndServiceImpl implements CallingAddressAndService, CAPAsnPrimitive {

    public static final int _ID_callingAddressValue = 0;
    public static final int _ID_serviceKey = 1;

    private static final String CALLING_ADDRESS_VALUE = "callingAddressValue";
    private static final String SERVICE_KEY = "serviceKey";

    public static final String _PrimitiveName = "CallingAddressAndService";

    private Digits callingAddressValue;
    private int serviceKey;

    public CallingAddressAndServiceImpl() {
    }

    public CallingAddressAndServiceImpl(Digits callingAddressValue, int serviceKey) {
        this.callingAddressValue = callingAddressValue;
        this.serviceKey = serviceKey;
    }

    public Digits getCallingAddressValue() {
        return callingAddressValue;
    }

    public int getServiceKey() {
        return serviceKey;
    }

    public int getTag() throws CAPException {
        return Tag.SEQUENCE;
    }

    public int getTagClass() {
        return Tag.CLASS_UNIVERSAL;
    }

    public boolean getIsPrimitive() {
        return false;
    }

    public void decodeAll(AsnInputStream ansIS) throws CAPParsingComponentException {
        try {
            int length = ansIS.readLength();
            this._decode(ansIS, length);
        } catch (IOException e) {
            throw new CAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    CAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new CAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    CAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    public void decodeData(AsnInputStream ansIS, int length) throws CAPParsingComponentException {
        try {
            this._decode(ansIS, length);
        } catch (IOException e) {
            throw new CAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    CAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new CAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    CAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    private void _decode(AsnInputStream ansIS, int length) throws IOException, AsnException, CAPParsingComponentException {

        this.callingAddressValue = null;
        this.serviceKey = 0;

        AsnInputStream ais = ansIS.readSequenceStreamData(length);

        while (true) {
            if (ais.available() == 0) {
                break;
            }

            int tag = ais.readTag();

            if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                switch (tag) {
                    case _ID_callingAddressValue: {
                        this.callingAddressValue = new DigitsImpl();
                        ((DigitsImpl) callingAddressValue).decodeAll(ais);
                        this.callingAddressValue.setIsGenericNumber();
                        break;
                    }
                    case _ID_serviceKey: {
                        if (!ais.isTagPrimitive()) {
                            throw new CAPParsingComponentException(
                                    "Error while decoding InitialDPRequest: Parameter " + _ID_serviceKey + " not primitive",
                                    CAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.serviceKey = (int) ais.readInteger();
                        break;
                    }
                    default: {
                        ais.advanceElement();
                        break;
                    }
                }
            } else {
                ais.advanceElement();
            }
        }

        if (this.serviceKey == 0) {
            throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": serviceKey is mandatory",
                    CAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    public void encodeAll(AsnOutputStream asnOs) throws CAPException {
        this.encodeAll(asnOs, this.getTagClass(), this.getTag());
    }

    public void encodeAll(AsnOutputStream asnOs, int tagClass, int tag) throws CAPException {
        try {
            asnOs.writeTag(tagClass, this.getIsPrimitive(), tag);
            int pos = asnOs.StartContentDefiniteLength();
            this.encodeData(asnOs);
            asnOs.FinalizeContent(pos);
        } catch (AsnException e) {
            throw new CAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    public void encodeData(AsnOutputStream asnOs) throws CAPException {

        if (this.callingAddressValue == null) {
            throw new CAPException("Error while encoding " + _PrimitiveName + ": callingAddressValue must not be null");
        }

        try {
            ((DigitsImpl) callingAddressValue).encodeAll(asnOs, Tag.CLASS_CONTEXT_SPECIFIC, _ID_callingAddressValue);
            asnOs.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _ID_serviceKey, this.serviceKey);
        } catch (IOException e) {
            throw new CAPException("IOException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (AsnException e) {
            throw new CAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    /**
     * XML Serialization/Deserialization
     */
    protected static final XMLFormat<CallingAddressAndServiceImpl> CALLING_ADDRESS_AND_SERVICE_XML = new XMLFormat<CallingAddressAndServiceImpl>(CallingAddressAndServiceImpl.class) {

        @Override
        public void read(javolution.xml.XMLFormat.InputElement xml, CallingAddressAndServiceImpl callingAddressAndServiceImpl) throws XMLStreamException {

            callingAddressAndServiceImpl.callingAddressValue = xml.get(CALLING_ADDRESS_VALUE, DigitsImpl.class);
            callingAddressAndServiceImpl.serviceKey = xml.get(SERVICE_KEY, Integer.class);
        }

        @Override
        public void write(CallingAddressAndServiceImpl callingAddressAndServiceImpl, javolution.xml.XMLFormat.OutputElement xml) throws XMLStreamException {

            xml.add((DigitsImpl) callingAddressAndServiceImpl.getCallingAddressValue(), CALLING_ADDRESS_VALUE, DigitsImpl.class);
            xml.add((Integer) callingAddressAndServiceImpl.getServiceKey(), SERVICE_KEY, Integer.class);
        }
    };

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        sb.append("callingAddressValue=");
        sb.append(callingAddressValue.toString());

        sb.append(", serviceKey=");
        sb.append(serviceKey);

        sb.append("]");

        return sb.toString();
    }

}
