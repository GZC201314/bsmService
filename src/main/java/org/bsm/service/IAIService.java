package org.bsm.service;

import org.bsm.pagemodel.AipFaceResult;
import org.bsm.pagemodel.PageUpload;
import org.bsm.pagemodel.PageUser;

public interface IAIService {

    public String uploadHeadIcon(PageUpload pageUpload);

    public AipFaceResult facelogin(PageUser pageUser);

    public AipFaceResult faceReg(PageUser pageUser);
}
