import { useContext, useEffect, useState } from "react";
import { MyUserContext } from "../configs/Context";
import { Card, Container, Button, Row, Col, Form, Spinner } from "react-bootstrap";
import Apis, { authApis, endpoints } from "../configs/Apis";
import cookie from "react-cookies";
import { toast } from "react-toastify";

const Profile = () => {
    const [user] = useContext(MyUserContext);
    const [serviceTypes, setServiceTypes] = useState([]);
    const [selectedTypes, setSelectedTypes] = useState([]); // để gửi request
    const [existingPermissions, setExistingPermissions] = useState([]); // quyền ACTIVE đã có
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const fetchData = async () => {
            try {
                // lấy danh sách serviceTypes
                const resTypes = await Apis.get(endpoints["service-types"]);
                if (Array.isArray(resTypes.data)) {
                    setServiceTypes(resTypes.data);
                } else {
                    setServiceTypes([]);
                }

                // nếu user là provider thì gọi API lấy permission hiện có
                if (user && user.role === "PROVIDER") {
                    const token = cookie.load("token");
                    const resPerms = await authApis(token).get(endpoints["provider-service-permissions"]);
                    if (Array.isArray(resPerms.data)) {
                        setExistingPermissions(resPerms.data);
                    }
                }
            } catch (err) {
                console.error("Fetch error", err);
                toast.error("Không thể tải dữ liệu quyền dịch vụ!");
            }
        };

        fetchData();
    }, [user]);

    const toggleType = (type) => {
        setSelectedTypes((prev) =>
            prev.includes(type)
                ? prev.filter((t) => t !== type)
                : [...prev, type]
        );
    };

    const handleRequestPermission = async () => {
        if (selectedTypes.length === 0) {
            toast.warning("Vui lòng chọn ít nhất một loại dịch vụ!");
            return;
        }

        try {
            setLoading(true);
            const token = cookie.load("token");
            const res = await authApis(token).post(endpoints["provider-request-permission"], {
                serviceTypes: selectedTypes,
            });

            toast.success(res.data.message || "Gửi yêu cầu thành công!");
            if (res.data.skipped?.length > 0) {
                toast.info(`Bỏ qua: ${res.data.skipped.join(", ")}`);
            }

            // Reset sau khi gửi
            setSelectedTypes([]);
        } catch (err) {
            console.error("Request failed", err);
            toast.error("Lỗi gửi yêu cầu cấp quyền!");
        } finally {
            setLoading(false);
        }
    };

    if (!user) {
        return (
            <Container className="mt-5 text-center">
                <p>Bạn chưa đăng nhập!</p>
            </Container>
        );
    }

    return (
        <Container className="mt-5">
            <Card className="p-4 shadow-sm">
                <h3 className="mb-4 text-center">Thông tin cá nhân</h3>
                <Row>
                    <Col md={4} className="text-center">
                        {user.avatar && (
                            <img
                                src={user.avatar}
                                alt="Avatar"
                                style={{
                                    width: "120px",
                                    height: "120px",
                                    objectFit: "cover",
                                    borderRadius: "50%",
                                    marginBottom: "1rem",
                                }}
                            />
                        )}
                        <Button variant="outline-primary" size="sm" className="mt-2 w-75 mx-auto d-block">
                            Chỉnh sửa thông tin
                        </Button>
                    </Col>

                    <Col md={8}>
                        <p><strong>Họ tên:</strong> {user.firstName} {user.lastName}</p>
                        <p><strong>Email:</strong> {user.email}</p>
                        <p><strong>SĐT:</strong> {user.phoneNumber}</p>
                        <p><strong>Vai trò:</strong> {user.role}</p>

                        {user.role === "PROVIDER" && user.provider && (
                            <>
                                <hr />
                                <h5>Thông tin nhà cung cấp</h5>
                                <p><strong>Tên công ty:</strong> {user.provider.companyName}</p>
                                <p><strong>Trạng thái:</strong> {user.provider.state}</p>
                                <p><strong>Ngày tạo:</strong>{" "}
                                    {user.provider.providerCreatedAt &&
                                        new Date(user.provider.providerCreatedAt.replace(" ", "T")).toLocaleDateString()}
                                </p>

                                <hr />
                                <h5>Yêu cầu cấp quyền dịch vụ</h5>
                                <p className="text-muted">
                                    Bạn có thể gửi yêu cầu đăng ký các loại dịch vụ như Tour, Vé xe, Phòng khách sạn, v.v.
                                </p>

                                {Array.isArray(serviceTypes) && serviceTypes.length > 0 ? (
                                    serviceTypes.map((type) => {
                                        const alreadyHas = existingPermissions.includes(type);
                                        return (
                                            <Form.Check
                                                key={type}
                                                type="checkbox"
                                                label={type}
                                                checked={alreadyHas || selectedTypes.includes(type)}
                                                disabled={alreadyHas} // readonly nếu đã có quyền
                                                onChange={() => toggleType(type)}
                                            />
                                        );
                                    })
                                ) : (
                                    <p className="text-muted">Không có loại dịch vụ nào để chọn</p>
                                )}

                                <Button
                                    variant="success"
                                    size="sm"
                                    className="mt-2"
                                    onClick={handleRequestPermission}
                                    disabled={loading}
                                >
                                    {loading ? <Spinner size="sm" animation="border" /> : "Gửi yêu cầu cấp quyền"}
                                </Button>
                            </>
                        )}

                        {user.role === "ADMIN" && (
                            <>
                                <hr />
                                <h5 className="text-danger">Tài khoản quản trị viên</h5>
                                <p>Bạn có toàn quyền quản lý hệ thống.</p>
                            </>
                        )}
                    </Col>
                </Row>
            </Card>
        </Container>
    );
};

export default Profile;
