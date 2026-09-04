const BASE_URL = '/api';

export async function predictColleges(predictionData) {
  const response = await fetch(`${BASE_URL}/predict`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(predictionData),
  });

  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.message || 'Failed to predict colleges');
  }
  return data;
}

export async function getCollegeByCode(collegeCode) {
  const response = await fetch(`${BASE_URL}/colleges/code/${collegeCode}`);
  if (!response.ok) {
    if (response.status === 404) {
      throw new Error(`College with code '${collegeCode}' not found`);
    }
    throw new Error('Failed to fetch college details');
  }
  return await response.json();
}

export async function getAllColleges() {
  const response = await fetch(`${BASE_URL}/colleges`);
  if (!response.ok) {
    throw new Error('Failed to fetch colleges');
  }
  return await response.json();
}
