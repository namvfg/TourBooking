import { Button, Spinner } from "react-bootstrap";

const ButtonSpinner = () => {
  return (
    <>
      <div class='mt-3 mb-3 text-center'>
        <Button variant='primary' disabled>
          <Spinner
            as='span'
            animation='grow'
            size='sm'
            role='status'
            aria-hidden='true'
          />
          Loading...
        </Button>
      </div>
    </>
  );
};

export default ButtonSpinner;
