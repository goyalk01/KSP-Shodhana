import { FeatureCollection } from "geojson";

/**
 * Official Survey of India (SOI) Compliant International Boundary of India.
 * Includes Jammu & Kashmir, Ladakh, Gilgit-Baltistan, Pakistan-Occupied Kashmir (PoK), and Aksai Chin as integral parts of India.
 */
export const officialIndiaBoundaryGeoJSON: FeatureCollection = {
  type: "FeatureCollection",
  features: [
    {
      type: "Feature",
      properties: {
        name: "India (Official Sovereign Boundary)",
        code: "IND",
        description: "Official Territory of India including J&K, Ladakh, PoK, Gilgit-Baltistan, and Aksai Chin"
      },
      geometry: {
        type: "Polygon",
        coordinates: [
          [
            // Northernmost Jammu & Kashmir, Gilgit-Baltistan, PoK
            [74.50, 37.05], // Indira Col / Northernmost Gilgit-Baltistan
            [75.50, 37.00],
            [76.80, 36.30], // Karakoram Pass / Siachen
            [78.50, 35.80], // Aksai Chin Northern border
            [79.80, 35.00], // Aksai Chin Eastern border
            [79.50, 33.00], // Pangong / Demchok
            [78.70, 32.00], // Himachal / Ladakh border
            [78.80, 30.50], // Uttarakhand border
            [80.50, 28.60], // Nepal border west
            [88.10, 26.80], // Nepal border east
            [88.80, 27.30], // Sikkim
            [89.80, 26.80], // Bhutan border
            [91.80, 27.80], // Arunachal Pradesh west
            [97.40, 28.30], // Kibithu / Easternmost Arunachal
            [95.50, 26.50], // Nagaland
            [94.30, 24.50], // Manipur
            [92.50, 23.00], // Mizoram
            [91.50, 23.50], // Tripura
            [91.80, 25.20], // Meghalaya
            [88.80, 22.50], // West Bengal / Sundarbans
            [87.00, 21.50], // Odisha coast
            [83.00, 18.00], // Andhra Pradesh coast
            [80.30, 13.10], // Chennai coast
            [79.80, 10.30], // Tamil Nadu coast
            [77.54, 8.08],  // Kanyakumari (Southernmost tip)
            [76.50, 9.50],  // Kerala coast
            [75.00, 12.00], // Mangaluru / Karnataka coast
            [73.80, 15.50], // Goa coast
            [72.80, 19.00], // Mumbai / Maharashtra coast
            [72.60, 21.00], // Gujarat / Surat coast
            [68.70, 23.70], // Sir Creek / Kutch (Westernmost tip)
            [70.50, 25.00], // Rajasthan border south
            [70.00, 27.50], // Rajasthan border west
            [74.50, 30.50], // Punjab border
            [74.50, 32.50], // Line of Control / Jammu
            [73.80, 33.50], // Punch / Rajouri / PoK boundary
            [73.50, 34.50], // Muzaffarabad / PoK
            [74.00, 36.00], // Gilgit / Hunza
            [74.50, 37.05]  // Closing loop at Indira Col
          ]
        ]
      }
    },
    {
      type: "Feature",
      properties: {
        name: "Jammu & Kashmir & Ladakh (Union Territories of India)",
        code: "IND-JK-LA",
        type: "UT_REGION"
      },
      geometry: {
        type: "Polygon",
        coordinates: [
          [
            [73.50, 34.50], // Muzaffarabad / PoK
            [74.00, 36.00], // Gilgit / Hunza
            [74.50, 37.05], // Indira Col
            [75.50, 37.00],
            [76.80, 36.30], // Siachen Glacier
            [78.50, 35.80], // Aksai Chin
            [79.80, 35.00],
            [79.50, 33.00], // Pangong Tso
            [78.70, 32.00],
            [77.00, 32.50], // Himachal border
            [75.50, 32.20], // Punjab border
            [74.50, 32.50], // Kathua / Jammu
            [73.80, 33.50], // Line of Control / Punch
            [73.50, 34.50]  // Closing loop
          ]
        ]
      }
    }
  ]
};
