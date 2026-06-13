type ErrorMessageLabelProps = {
  error: string | null;
};

const ErrorMessageLabel = ({ error }: ErrorMessageLabelProps) => {
  return <>{error && <p className="mt-4 text-sm text-red-600">{error}</p>}</>;
};
export default ErrorMessageLabel;
