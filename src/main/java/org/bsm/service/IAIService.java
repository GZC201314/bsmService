package org.bsm.service;

import org.bsm.pagemodel.AipFaceResult;
import org.bsm.pagemodel.FaceLib;
import org.bsm.pagemodel.PageUpload;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface IAIService {

    public String ocr(PageUpload pageUpload, HttpServletResponse response);

    public AipFaceResult facelogin(PageUpload pageUpload) throws IOException;

    public boolean faceReg(PageUpload pageUpload) throws IOException;

    public List<FaceLib> getallFaceLib(PageUpload pageUpload) throws IOException;


}
