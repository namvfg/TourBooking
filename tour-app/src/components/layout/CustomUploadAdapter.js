import { authApis, endpoints } from "../configs/Apis";
import cookies from "react-cookies";

export const CustomUploadAdapter = (loader) => {
    const token = cookies.load("token");

    return {
        upload: async () => {
            const file = await loader.file;
            const formData = new FormData();
            formData.append("upload", file);

            const res = await authApis(token).post(endpoints["upload-image"], formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            });

            return {
                default: res.data.url, // CKEditor expects a "default" field
            };
        }
    };
};

// This is the plugin that injects the adapter
export function UploadAdapterPlugin(editor) {
    editor.plugins.get("FileRepository").createUploadAdapter = (loader) => {
        return CustomUploadAdapter(loader); // 
    };
}
