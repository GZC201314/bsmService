package org.bsm.service;

import org.bsm.pagemodel.AipFaceResult;
import org.bsm.pagemodel.PageUpload;

import java.io.IOException;

public interface IAIService {

    public String ocr(PageUpload pageUpload);

    public AipFaceResult facelogin(PageUpload pageUpload) throws IOException;

    public AipFaceResult faceReg(PageUpload pageUpload) throws IOException;
}
