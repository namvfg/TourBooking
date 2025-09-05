import { Alert } from "react-bootstrap";

const MyAlert = ({ variant = "danger", message, onClose }) => {
  if (!message) return null;

  return (
    <Alert variant={variant} dismissible onClose={onClose} className="mt-2">
      {message}
    </Alert>
  );
};

export default MyAlert;
