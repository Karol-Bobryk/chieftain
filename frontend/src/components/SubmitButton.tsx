type SubmitButtonProps = {
  displayedText: string;
  isEnabled: boolean;
};

const SubmitButton = ({ displayedText, isEnabled }: SubmitButtonProps) => {
  return (
    <button
      type="submit"
      disabled={!isEnabled}
      className="h-11 w-full rounded-xl bg-zinc-900 text-sm font-medium text-white transition hover:bg-zinc-800 disabled:opacity-50"
    >
      {displayedText}
    </button>
  );
};
export default SubmitButton;
