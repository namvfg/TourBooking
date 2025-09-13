import React, { useState, useEffect, useContext } from "react";
import { MyUserContext } from '../configs/Context';
import { authApis, endpoints } from '../configs/Apis';
import { Form, Button, Alert, Spinner } from "react-bootstrap";
import { CKEditor } from "@ckeditor/ckeditor5-react";
import CustomEditor from '../../ckeditor/build/ckeditor';
import { UploadAdapterPlugin } from "../layout/CustomUploadAdapter";
import cookies from 'react-cookies';
import { useNavigate } from "react-router-dom";

const TRANSPORT_ENUMS = [
    { value: "BUS", label: "Xe Bus" },
    { value: "PLANE", label: "Máy bay" },
    { value: "SHIP", label: "Tàu thuỷ" }
];

const AddServicePost = () => {
    const [user] = useContext(MyUserContext);
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [imageFile, setImageFile] = useState(null);
    const [price, setPrice] = useState("");
    const [availableSlot, setAvailableSlot] = useState("");
    const [address, setAddress] = useState("");
    const [serviceType, setServiceType] = useState("");
    const [err, setErr] = useState("");
    const [success, setSuccess] = useState("");
    const [loading, setLoading] = useState(false);
    const [serviceTypeOptions, setServiceTypeOptions] = useState([]);

    // Thêm các trường chi tiết động
    const [roomStartDate, setRoomStartDate] = useState("");
    const [roomEndDate, setRoomEndDate] = useState("");
    const [tourStartDate, setTourStartDate] = useState("");
    const [tourEndDate, setTourEndDate] = useState("");
    const [transportType, setTransportType] = useState("");
    const [transportStartDate, setTransportStartDate] = useState("");
    const [destination, setDestination] = useState("");

    const providerId = user?.provider?.providerId;
    const token = cookies.load("token");
    const navigate = useNavigate();

    useEffect(() => {
        const fetchAllowedServiceTypes = async () => {
            try {
                const res = await authApis(token).get(endpoints["provider-service-permissions"]);
                setServiceTypeOptions(res.data);
            } catch (e) {
                console.error("Lỗi load quyền dịch vụ:", e);
                setServiceTypeOptions([]);
            }
        };
        if (token) fetchAllowedServiceTypes();
    }, [token]);

    if (!user || user.role !== "PROVIDER") {
        return <Alert variant="danger">Chỉ nhà cung cấp mới được thêm dịch vụ!</Alert>;
    }

    const handleFileChange = (e) => {
        setImageFile(e.target.files?.[0] ?? null);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErr(""); setSuccess("");

        if (!name.trim()) return setErr("Tên dịch vụ không được rỗng!");
        if (!description.trim()) return setErr("Mô tả không được rỗng!");
        if (!imageFile) return setErr("Vui lòng chọn ảnh dịch vụ!");
        if (!price || isNaN(price)) return setErr("Giá phải là số!");
        if (!availableSlot || isNaN(availableSlot)) return setErr("Số slot phải là số!");
        if (!address.trim()) return setErr("Địa chỉ không được rỗng!");
        if (!serviceType) return setErr("Vui lòng chọn loại dịch vụ!");
        if (!providerId || isNaN(providerId)) return setErr("Provider không hợp lệ!");

        // Validate các trường động theo loại dịch vụ
        if (serviceType === "ROOM") {
            if (!roomStartDate) return setErr("Vui lòng chọn ngày bắt đầu phòng!");
            // roomEndDate có thể null
        } else if (serviceType === "TOUR") {
            if (!tourStartDate) return setErr("Vui lòng chọn ngày bắt đầu tour!");
            // tourEndDate có thể null
        } else if (serviceType === "TRANSPORTATION") {
            if (!transportType) return setErr("Vui lòng chọn loại phương tiện!");
            if (!transportStartDate) return setErr("Vui lòng chọn ngày khởi hành!");
            if (!destination.trim()) return setErr("Vui lòng nhập điểm đến!");
        }

        setLoading(true);

        try {
            const formData = new FormData();
            formData.append("name", name);
            formData.append("description", description);
            formData.append("image", imageFile);
            formData.append("price", price.toString());
            formData.append("availableSlot", parseInt(availableSlot));
            formData.append("address", address);
            formData.append("serviceType", serviceType);
            formData.append("serviceProviderId", parseInt(providerId));

            // Gửi các trường detail tuỳ loại
            if (serviceType === "ROOM") {
                formData.append("roomStartDate", roomStartDate);
                if (roomEndDate) formData.append("roomEndDate", roomEndDate);
            } else if (serviceType === "TOUR") {
                formData.append("tourStartDate", tourStartDate);
                if (tourEndDate) formData.append("tourEndDate", tourEndDate);
            } else if (serviceType === "TRANSPORTATION") {
                formData.append("transportType", transportType);
                formData.append("transportStartDate", transportStartDate);
                formData.append("destination", destination);
            }

            await authApis(token).post(endpoints["service-post-add"], formData, {
                headers: { "Content-Type": "multipart/form-data" }
            });

            setSuccess("Thêm dịch vụ thành công!");
            setName(""); setDescription(""); setImageFile(null); setPrice(""); setAvailableSlot(""); setAddress(""); setServiceType("");

            // Reset các trường detail
            setRoomStartDate(""); setRoomEndDate("");
            setTourStartDate(""); setTourEndDate("");
            setTransportType(""); setTransportStartDate(""); setDestination("");

            // Chuyển về trang chủ sau khi thêm thành công
            setTimeout(() => {
                navigate("/");
            }, 1200);
        } catch (error) {
            let msg = "Lỗi thêm dịch vụ";
            if (error.response?.data?.startsWith?.("<!DOCTYPE html"))
                msg = "Yêu cầu không hợp lệ hoặc dữ liệu gửi lên sai định dạng!";
            else if (error.response?.data)
                msg = error.response.data;
            setErr(msg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="mb-5 p-4 border rounded shadow-sm bg-light">
            <h3>Thêm dịch vụ mới</h3>
            {err && <Alert variant="danger">{err}</Alert>}
            {success && <Alert variant="success">{success}</Alert>}
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                    <Form.Label>Tên dịch vụ</Form.Label>
                    <Form.Control value={name} onChange={e => setName(e.target.value)} required />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Mô tả</Form.Label>
                    <CKEditor
                        editor={CustomEditor}
                        data={description}
                        onReady={(editor) => {
                            UploadAdapterPlugin(editor);
                            editor.editing.view.change((writer) => {
                                const root = editor.editing.view.document.getRoot();
                                writer.setStyle("min-height", "400px", root);
                                writer.setStyle("max-height", "600px", root);
                                writer.setStyle("overflow-y", "auto", root);
                                writer.setStyle("padding", "10px", root);
                                writer.setStyle("border", "1px solid #ccc", root);
                                writer.setStyle("border-radius", "6px", root);
                            });
                        }}
                        onChange={(event, editor) => setDescription(editor.getData())}
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Ảnh dịch vụ</Form.Label>
                    <Form.Control type="file" accept="image/*" onChange={handleFileChange} required />
                    {imageFile && (
                        <div className="mt-2">
                            <span>Đã chọn: <b>{imageFile.name}</b></span>
                            <img
                                src={URL.createObjectURL(imageFile)}
                                alt="Preview"
                                style={{ maxWidth: 200, marginTop: 10, borderRadius: 8 }}
                            />
                        </div>
                    )}
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Giá (VNĐ)</Form.Label>
                    <Form.Control type="number" value={price} onChange={e => setPrice(e.target.value)} required />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Số slot còn</Form.Label>
                    <Form.Control type="number" value={availableSlot} onChange={e => setAvailableSlot(e.target.value)} required />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Địa chỉ</Form.Label>
                    <Form.Control value={address} onChange={e => setAddress(e.target.value)} required />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Loại dịch vụ</Form.Label>
                    <Form.Select value={serviceType} onChange={e => setServiceType(e.target.value)} required>
                        <option value="">-- Chọn loại dịch vụ --</option>
                        {serviceTypeOptions.map(type => (
                            <option key={type} value={type}>{type}</option>
                        ))}
                    </Form.Select>
                </Form.Group>

                {/* Các trường chi tiết động theo loại dịch vụ */}
                {serviceType === "ROOM" && (
                    <>
                        <Form.Group className="mb-3">
                            <Form.Label>Ngày bắt đầu phòng</Form.Label>
                            <Form.Control type="datetime-local" value={roomStartDate} onChange={e => setRoomStartDate(e.target.value)} required />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Ngày kết thúc phòng</Form.Label>
                            <Form.Control type="datetime-local" value={roomEndDate} onChange={e => setRoomEndDate(e.target.value)} />
                        </Form.Group>
                    </>
                )}
                {serviceType === "TOUR" && (
                    <>
                        <Form.Group className="mb-3">
                            <Form.Label>Ngày bắt đầu tour</Form.Label>
                            <Form.Control type="datetime-local" value={tourStartDate} onChange={e => setTourStartDate(e.target.value)} required />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Ngày kết thúc tour</Form.Label>
                            <Form.Control type="datetime-local" value={tourEndDate} onChange={e => setTourEndDate(e.target.value)} />
                        </Form.Group>
                    </>
                )}
                {serviceType === "TRANSPORTATION" && (
                    <>
                        <Form.Group className="mb-3">
                            <Form.Label>Loại phương tiện</Form.Label>
                            <Form.Select value={transportType} onChange={e => setTransportType(e.target.value)} required>
                                <option value="">-- Chọn loại phương tiện --</option>
                                {TRANSPORT_ENUMS.map(opt => (
                                    <option value={opt.value} key={opt.value}>{opt.label}</option>
                                ))}
                            </Form.Select>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Ngày khởi hành</Form.Label>
                            <Form.Control type="datetime-local" value={transportStartDate} onChange={e => setTransportStartDate(e.target.value)} required />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Điểm đến</Form.Label>
                            <Form.Control type="text" value={destination} onChange={e => setDestination(e.target.value)} required />
                        </Form.Group>
                    </>
                )}

                <Button type="submit" variant="primary" disabled={loading}>
                    {loading ? <Spinner size="sm" /> : "Thêm dịch vụ"}
                </Button>
            </Form>
        </div>
    );
};

export default AddServicePost;