// Generate color palette based on theme name
export const generateColorPalette = (themeName) => {
  let hash = 0;
  for (let i = 0; i < themeName.length; i++) {
    hash = themeName.charCodeAt(i) + ((hash << 5) - hash);
  }

  // Generate stable HSL values
  const hue = Math.abs(hash % 360);
  const saturation = 45 + (Math.abs(hash) % 15); // slightly toned-down 45–60%
  const lightness = 40 + (Math.abs(hash >> 8) % 10); // darker 40–50%

  const primary = `hsl(${hue}, ${saturation}%, ${lightness}%)`;
  const secondary = `hsl(${(hue + 25) % 360}, ${saturation + 5}%, ${lightness + 8}%)`;
  const accent = `hsl(${(hue + 60) % 360}, ${saturation + 10}%, ${lightness + 15}%)`;
  const dark = `hsl(${hue}, ${saturation + 15}%, ${lightness - 15}%)`;
  const light = `hsl(${hue}, ${saturation - 10}%, ${lightness + 20}%)`;

  return { primary, secondary, accent, light, dark };
};