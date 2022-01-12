package us.supercheng.services.impl;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import us.supercheng.services.FastDFSService;

@Service
public class FastDFSServiceImpl implements FastDFSService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Transactional
    @Override
    public String uploadFile(MultipartFile file, String extension) throws Exception {
        StorePath p = fastFileStorageClient.uploadFile(file.getInputStream(),
                file.getSize(), extension, null);
        return p.getFullPath();
    }
}
