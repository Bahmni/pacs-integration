package org.bahmni.module.pacsintegration.atomfeed.contract.hl7;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HL7CodedElement {
    private String identifier;
    private String text;
    private String nameOfCodingSystem;
}
